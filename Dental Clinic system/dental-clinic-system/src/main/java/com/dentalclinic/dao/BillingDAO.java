package com.dentalclinic.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.dentalclinic.model.Bill;
import com.dentalclinic.util.DBConnection;

public class BillingDAO {

    // insert new bill, return it with generated id
    public Bill save(Bill bill) throws Exception {

        String sql = "INSERT INTO bills (amount, appointment_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, bill.getAmount());
            stmt.setInt(2, bill.getAppointmentId());

            stmt.executeUpdate();

            // grab the new id
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                bill.setBillId(keys.getInt(1));
            }

            return bill;
        }
    }

    // find bill by appointment id - one appointment has at most one bill
    public Bill findByAppointmentId(int appointmentId) throws Exception {

        String sql = "SELECT * FROM bills WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);

            ResultSet rs = stmt.executeQuery();

            // build the bill if one exists for this appointment
            if (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setAmount(rs.getDouble("amount"));
                bill.setAppointmentId(rs.getInt("appointment_id"));
                return bill;
            }

            return null;
        }
    }

    // sums total revenue from bills issued within a date range
    public double sumRevenueInRange(LocalDate startDate, LocalDate endDate) throws Exception {

        // COALESCE so we get 0 instead of null when there are no bills in range
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM bills "
                   + "WHERE DATE(issued_at) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            // return the total, or zero if the query gave nothing back
            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;
        }
    }
}