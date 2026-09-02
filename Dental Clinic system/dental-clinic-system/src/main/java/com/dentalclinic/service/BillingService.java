package com.dentalclinic.service;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.dao.BillingDAO;
import com.dentalclinic.dao.TreatmentDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.TreatmentType;

public class BillingService {

    private BillingDAO billingDAO = new BillingDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private TreatmentDAO treatmentDAO = new TreatmentDAO();

    // calculate and save a bill for an appointment
    public Bill calculateBill(int appointmentId) throws Exception {

        Appointment appointment = appointmentDAO.findById(appointmentId);

        if (appointment == null) {
            throw new IllegalStateException("Appointment not found");
        }

        // can't bill a cancelled appointment
        if (appointment.getStatus().equals("CANCELLED")) {
            throw new IllegalStateException("Cannot bill a cancelled appointment");
        }

        // already billed - don't create a duplicate
        Bill existing = billingDAO.findByAppointmentId(appointmentId);
        if (existing != null) {
            throw new IllegalStateException("This appointment has already been billed");
        }

        // billing means the treatment actually happened - mark it completed
        // only if it's still SCHEDULED, so we don't touch an already-completed one
        if (appointment.getStatus().equals("SCHEDULED")) {
            appointment.setStatus("COMPLETED");
            appointmentDAO.update(appointment);
        }

        // get the treatment type to know the cost
        TreatmentType treatment = treatmentDAO.findById(appointment.getTreatmentTypeId());

        Bill bill = new Bill();
        bill.setAmount(treatment.getCost());
        bill.setAppointmentId(appointmentId);

        return billingDAO.save(bill);
    }

    // get bill for printing
    public Bill getBillForAppointment(int appointmentId) throws Exception {
        return billingDAO.findByAppointmentId(appointmentId);
    }
}