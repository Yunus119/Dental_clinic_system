package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.dentalclinic.dao.UserDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;

public class BillingServiceTest {

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
    public void testCalculateBillSucceeds() throws Exception {

        BillingService billingService = new BillingService();
        AppointmentService appointmentService = new AppointmentService();
        PatientService patientService = new PatientService();
        TreatmentService treatmentService = new TreatmentService();
        UserDAO userDAO = new UserDAO();

        String unique = String.valueOf(System.currentTimeMillis());

        Doctor doctor = new Doctor(0, "billdoc_" + unique, "hashedpw", "Bill", "Doctor", unique + "@clinic.com");
        Doctor savedDoctor = (Doctor) userDAO.save(doctor);

        Patient patient = patientService.createPatient("Bill", "PatientOne" + randomLetters(6), "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment" + unique, 5000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDate.now().plusDays(1), 4);

        Bill bill = billingService.calculateBill(appointment.getAppointmentId());

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

        Patient patient = patientService.createPatient("Bill", "PatientTwo" + randomLetters(6), "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment2" + unique, 3000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDate.now().plusDays(2), 5);

        appointmentService.cancelAppointment(appointment.getAppointmentId());

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

        Patient patient = patientService.createPatient("Bill", "PatientThree" + randomLetters(6), "0771234567", "Test Address");

        TreatmentType treatment = treatmentService.createTreatmentType("BillTreatment3" + unique, 2000.00);

        Appointment appointment = appointmentService.makeAppointment(
                patient.getPatientId(), savedDoctor.getUserId(), treatment.getTreatmentTypeId(),
                LocalDate.now().plusDays(3), 6);

        billingService.calculateBill(appointment.getAppointmentId());

        assertThrows(IllegalStateException.class, () -> {
            billingService.calculateBill(appointment.getAppointmentId());
        });
    }
}