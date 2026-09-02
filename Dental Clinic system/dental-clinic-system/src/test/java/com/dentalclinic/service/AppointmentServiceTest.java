package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.dao.UserDAO;

public class AppointmentServiceTest {

    // generates random letters, since patient names can only contain letters
    private static String randomLetters(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(rnd.nextInt(letters.length())));
        }
        return sb.toString();
    }

    @Test
    public void testMakeAppointmentSucceeds() throws Exception {

        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "doc_" + unique, "hashedpw", "Test", "Doctor", unique + "@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Test", "PatientOne" + randomLetters(6), "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("TestTreatment" + unique, 100.00);

        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        int slotNumber = 1;

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

        Patient patientA = patientService.createPatient("PatientA", randomLetters(8), "0771111111", "Address A");
        Patient patientB = patientService.createPatient("PatientB", randomLetters(8), "0772222222", "Address B");

        TreatmentType treatment = treatmentService.createTreatmentType("TestTreatment2" + unique, 100.00);

        LocalDate appointmentDate = LocalDate.now().plusDays(2);
        int slotNumber = 2;

        appointmentService.makeAppointment(
                patientA.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                appointmentDate, slotNumber);

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

        Patient patient = patientService.createPatient("Test", "PatientSearch" + randomLetters(6), "0773333333", "Address");
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