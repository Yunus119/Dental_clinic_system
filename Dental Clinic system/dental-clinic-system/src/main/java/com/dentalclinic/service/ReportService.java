package com.dentalclinic.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.dao.BillingDAO;

public class ReportService {

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private BillingDAO billingDAO = new BillingDAO();

    // total appointments in a date range
    public int getAppointmentCount(LocalDate startDate, LocalDate endDate) throws Exception {
        return appointmentDAO.countAppointmentsInRange(startDate, endDate);
    }

    // total revenue in a date range
    public double getRevenue(LocalDate startDate, LocalDate endDate) throws Exception {
        return billingDAO.sumRevenueInRange(startDate, endDate);
    }

    // most booked treatments in a date range
    public List<TreatmentPopularity> getTreatmentPopularity(LocalDate startDate, LocalDate endDate) throws Exception {
        return appointmentDAO.getMostRequestedTreatments(startDate, endDate);
    }

    // builds a downloadable CSV report as raw bytes
    // matches the generateReport(): byte[] signature from the class diagram
    public byte[] generateReport(LocalDate startDate, LocalDate endDate) throws Exception {

        StringBuilder csv = new StringBuilder();

        // header section with the report period
        csv.append("Dental Clinic Report\n");
        csv.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");

        // summary numbers for the period
        csv.append("Total Appointments,").append(getAppointmentCount(startDate, endDate)).append("\n");
        csv.append("Total Revenue,Rs. ").append(getRevenue(startDate, endDate)).append("\n\n");

        // one row per treatment type, showing how many times it was booked
        csv.append("Treatment Type,Times Booked\n");
        for (TreatmentPopularity t : getTreatmentPopularity(startDate, endDate)) {
            csv.append(t.getTreatmentName()).append(",").append(t.getTimesBooked()).append("\n");
        }

        // convert the CSV text into bytes so it can be downloaded
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(csv.toString().getBytes(StandardCharsets.UTF_8));

        return out.toByteArray();
    }
}