package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.dao.UserDAO;

public class AppointmentServiceTest {

    @Test
    public void testMakeAppointmentSucceeds() throws Exception {

        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "doc_" + unique, "hashedpw", "Test", "Doctor", unique + "@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Test", "Patient" + unique, "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("TestTreatment" + unique, 100.00);

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        int slotNumber = 1;   // first morning slot, 9:00 AM

        Appointment booked = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                appointmentDate, slotNumber);

        assertTrue(booked.getAppointmentId() > 0);
        assertEquals("SCHEDULED", booked.getStatus());
        assertEquals(slotNumber, booked.getAppointmentNumber());
    }

    @Test
    public void testMakeAppointmentRejectsConflict() throws Exception {

        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "doc2_" + unique, "hashedpw", "Test", "Doctor2", unique + "b@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patientA = patientService.createPatient("PatientA", unique, "0771111111", "Address A");
        Patient patientB = patientService.createPatient("PatientB", unique, "0772222222", "Address B");

        TreatmentType treatment = treatmentService.createTreatmentType("TestTreatment2" + unique, 100.00);

        LocalDate appointmentDate = LocalDate.now().plusDays(2);
        int slotNumber = 2;   // second morning slot, 9:30 AM

        // book the first appointment - should succeed
        appointmentService.makeAppointment(
                patientA.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                appointmentDate, slotNumber);

        // try booking the SAME doctor, SAME slot, different patient - should throw
        assertThrows(IllegalStateException.class, () -> {
            appointmentService.makeAppointment(
                    patientB.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                    appointmentDate, slotNumber);
        });
    }

    @Test
    public void testSearchAppointmentByDoctorAndDate() throws Exception {

        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "doc3_" + unique, "hashedpw", "Test", "Doctor3", unique + "c@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Test", "SearchPatient" + unique, "0773333333", "Address");
        TreatmentType treatment = treatmentService.createTreatmentType("TestTreatment3" + unique, 100.00);

        LocalDate appointmentDate = LocalDate.now().plusDays(3);
        int slotNumber = 3;

        appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                appointmentDate, slotNumber);

        List<Appointment> results = appointmentService.searchAppointment(savedDoctor.getUserId(), appointmentDate);

        assertEquals(1, results.size());
        assertEquals(savedDoctor.getUserId(), results.get(0).getDoctorId());
    }
}