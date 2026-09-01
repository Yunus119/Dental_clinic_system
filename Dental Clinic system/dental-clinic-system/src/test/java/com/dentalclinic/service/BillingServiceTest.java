package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.dentalclinic.dao.UserDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;

public class BillingServiceTest {

    @Test
    public void testCalculateBillSucceeds() throws Exception {

        BillingService billingService = new BillingService();
        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        // set up a doctor, patient, treatment, and appointment to bill against
        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "billdoc_" + unique, "hashedpw", "Bill", "Doctor", unique + "@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Bill", "Patient" + unique, "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment" + unique, 5000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDateTime.now().plusDays(1));

        // calculate the bill
        Bill bill = billingService.calculateBill(appointment.getAppointmentId());

        // should have a real id, and match the treatment cost
        assertTrue(bill.getBillId() > 0);
        assertEquals(5000.00, bill.getAmount());
        assertEquals(appointment.getAppointmentId(), bill.getAppointmentId());
    }

    @Test
    public void testCalculateBillRejectsCancelledAppointment() throws Exception {

        BillingService billingService = new BillingService();
        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "billdoc2_" + unique, "hashedpw", "Bill", "Doctor2", unique + "b@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Bill", "Patient2" + unique, "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment2" + unique, 3000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDateTime.now().plusDays(2));

        // cancel it
        appointmentService.cancelAppointment(appointment.getAppointmentId());

        // trying to bill a cancelled appointment should throw
        assertThrows(IllegalStateException.class, () -> {
            billingService.calculateBill(appointment.getAppointmentId());
        });
    }

    @Test
    public void testCalculateBillRejectsDuplicateBilling() throws Exception {

        BillingService billingService = new BillingService();
        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "billdoc3_" + unique, "hashedpw", "Bill", "Doctor3", unique + "c@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Bill", "Patient3" + unique, "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment3" + unique, 2000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDateTime.now().plusDays(3));

        // bill it once - should work
        billingService.calculateBill(appointment.getAppointmentId());

        // bill the same appointment again - should throw
        assertThrows(IllegalStateException.class, () -> {
            billingService.calculateBill(appointment.getAppointmentId());
        });
    }
}