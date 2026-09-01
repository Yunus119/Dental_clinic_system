package com.dentalclinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.util.DBConnection;
import com.dentalclinic.service.TreatmentPopularity;

public class AppointmentDAO {

    // checks if this doctor already has an appointment at this exact time
    public boolean existsConflict(int doctorId, LocalDateTime dateTime) throws Exception {

        String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_datetime = ? AND status != 'CANCELLED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    // insert new appointment, return it with generated id
    public Appointment save(Appointment appointment) throws Exception {

        String sql = "INSERT INTO appointments (appointment_number, status, appointment_datetime, patient_id, doctor_id, treatment_type_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, appointment.getAppointmentNumber());
            stmt.setString(2, appointment.getStatus());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            stmt.setInt(4, appointment.getPatientId());
            stmt.setInt(5, appointment.getDoctorId());
            stmt.setInt(6, appointment.getTreatmentTypeId());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                appointment.setAppointmentId(keys.getInt(1));
            }

            return appointment;
        }
    }

    // update appointment (used for reschedule/status change)
    public void update(Appointment appointment) throws Exception {

        String sql = "UPDATE appointments SET status = ?, appointment_datetime = ?, doctor_id = ?, treatment_type_id = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getStatus());
            stmt.setTimestamp(2, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            stmt.setInt(3, appointment.getDoctorId());
            stmt.setInt(4, appointment.getTreatmentTypeId());
            stmt.setInt(5, appointment.getAppointmentId());

            stmt.executeUpdate();
        }
    }

    // cancel an appointment - just flips the status, doesn't delete the row
    public void cancel(int appointmentId) throws Exception {

        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
        }
    }

    // find all appointments for a specific doctor
    public List<Appointment> findByDoctor(int doctorId) throws Exception {

        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }
    
    // find appointments for a doctor within a date range (inclusive)
    public List<Appointment> findByDoctorAndDateRange(int doctorId, LocalDate startDate, LocalDate endDate) throws Exception {

        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? "
                   + "AND DATE(appointment_datetime) BETWEEN ? AND ? "
                   + "ORDER BY appointment_datetime";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }

    // find one appointment by its id - used when calculating a bill
    public Appointment findById(int appointmentId) throws Exception {

        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;
        }
    }

    private Appointment mapRow(ResultSet rs) throws Exception {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getInt("appointment_number"));
        a.setStatus(rs.getString("status"));
        a.setAppointmentDateTime(rs.getTimestamp("appointment_datetime").toLocalDateTime());
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setTreatmentTypeId(rs.getInt("treatment_type_id"));
        return a;
    }
    
    // finds the highest appointment number already used on this specific date
    // returns 0 if there are no appointments that day yet
    public int getMaxAppointmentNumberForDate(LocalDate date) throws Exception {

    	String sql = "SELECT MAX(appointment_number) AS max_number FROM appointments WHERE DATE(appointment_datetime) = ?";

    	try (Connection conn = DBConnection.getConnection();
    			PreparedStatement stmt = conn.prepareStatement(sql)) {

    		stmt.setDate(1, java.sql.Date.valueOf(date));

    		ResultSet rs = stmt.executeQuery();

    		if (rs.next()) {
    			// MAX() returns NULL if there are no rows at all, not 0
    			int max = rs.getInt("max_number");
    			return rs.wasNull() ? 0 : max;
    		}

    		return 0;
    	}
    }
    
    // find appointments for a doctor on a specific date
    public List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) throws Exception {

        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND DATE(appointment_datetime) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }
    
 // counts appointments within a date range
    public int countAppointmentsInRange(LocalDate startDate, LocalDate endDate) throws Exception {

        String sql = "SELECT COUNT(*) AS total FROM appointments WHERE DATE(appointment_datetime) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    // counts how many times each treatment type was booked in a date range, most popular first
    public List<TreatmentPopularity> getMostRequestedTreatments(LocalDate startDate, LocalDate endDate) throws Exception {

        List<TreatmentPopularity> results = new ArrayList<>();

        String sql = "SELECT tt.name, COUNT(*) AS times_booked "
                   + "FROM appointments a "
                   + "JOIN treatment_types tt ON a.treatment_type_id = tt.treatment_type_id "
                   + "WHERE DATE(a.appointment_datetime) BETWEEN ? AND ? "
                   + "GROUP BY tt.treatment_type_id, tt.name "
                   + "ORDER BY times_booked DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new TreatmentPopularity(rs.getString("name"), rs.getInt("times_booked")));
            }

            return results;
        }
    }
}