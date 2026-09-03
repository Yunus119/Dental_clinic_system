package com.dentalclinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.service.AppointmentListItem;
import com.dentalclinic.service.TreatmentPopularity;
import com.dentalclinic.util.DBConnection;

public class AppointmentDAO {

    // checks if doctor already booked at this exact time
    public boolean existsConflict(int doctorId, LocalDateTime dateTime) throws Exception {

        // ignore cancelled appointments when checking for a clash
        String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_datetime = ? AND status != 'CANCELLED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));

            ResultSet rs = stmt.executeQuery();

            // true if a row came back
            return rs.next();
        }
    }

    // insert new appointment, return with generated id
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

            // grab the new id
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                appointment.setAppointmentId(keys.getInt(1));
            }

            return appointment;
        }
    }

    // update appointment - status change, reschedule etc
    // includes appointment_number now too, since rescheduling changes the slot
    public void update(Appointment appointment) throws Exception {

        String sql = "UPDATE appointments SET status = ?, appointment_datetime = ?, appointment_number = ?, doctor_id = ?, treatment_type_id = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointment.getStatus());
            stmt.setTimestamp(2, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            stmt.setInt(3, appointment.getAppointmentNumber());
            stmt.setInt(4, appointment.getDoctorId());
            stmt.setInt(5, appointment.getTreatmentTypeId());
            stmt.setInt(6, appointment.getAppointmentId());

            stmt.executeUpdate();
        }
    }

    // cancel - just flips status, doesn't delete the row
    public void cancel(int appointmentId) throws Exception {

        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
        }
    }

    // find all appointments for one doctor
    // kept for the IAppointmentViewer interface, not called by any current page
    public List<Appointment> findByDoctor(int doctorId) throws Exception {

        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            // go through each row and turn it into an Appointment
            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }

    // find one appointment by id - used for billing and update appointment
    public Appointment findById(int appointmentId) throws Exception {

        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);

            ResultSet rs = stmt.executeQuery();

            // return the appointment if found, otherwise null
            if (rs.next()) {
                return mapRow(rs);
            }

            return null;
        }
    }

    // find one appointment with patient/doctor names already joined in
    // not currently called anywhere - findById + AppointmentService covers this instead
    public AppointmentListItem findItemById(int appointmentId) throws Exception {

        // join patient and doctor tables so we get names in one query
        String sql = "SELECT a.appointment_id, a.appointment_number, a.appointment_datetime, a.status, "
                + "a.patient_id, a.doctor_id, a.treatment_type_id, "
                + "p.first_name AS patient_first, p.last_name AS patient_last, "
                + "u.first_name AS doctor_first, u.last_name AS doctor_last, u.email AS doctor_email "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN users u ON a.doctor_id = u.user_id "
                + "WHERE a.appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);

            ResultSet rs = stmt.executeQuery();

            // build the display item directly from the joined row
            if (rs.next()) {
                return new AppointmentListItem(
                        rs.getInt("appointment_id"),
                        rs.getInt("appointment_number"),
                        rs.getTimestamp("appointment_datetime").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getString("patient_first") + " " + rs.getString("patient_last"),
                        rs.getString("doctor_first") + " " + rs.getString("doctor_last"),
                        rs.getString("doctor_email")
                );
            }

            return null;
        }
    }

    // find appointments for a doctor on one specific date - used by the booking schedule grid
    public List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) throws Exception {

        List<Appointment> results = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND DATE(appointment_datetime) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();

            // collect every appointment that matches the doctor and date
            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }

    // turns one row into an Appointment object
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

    // counts appointments in a date range - used by reports
    public int countAppointmentsInRange(LocalDate startDate, LocalDate endDate) throws Exception {

        String sql = "SELECT COUNT(*) AS total FROM appointments WHERE DATE(appointment_datetime) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            // return the count, or zero if nothing came back
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    // counts how many times each treatment was booked, most popular first - used by reports
    public List<TreatmentPopularity> getMostRequestedTreatments(LocalDate startDate, LocalDate endDate) throws Exception {

        List<TreatmentPopularity> results = new ArrayList<>();

        // group by treatment and count bookings, highest first
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

            // add one entry per treatment type returned
            while (rs.next()) {
                results.add(new TreatmentPopularity(rs.getString("name"), rs.getInt("times_booked")));
            }

            return results;
        }
    }

    // main search used by the appointment list page - combines whichever filters are given
    // lockedDoctorId forces one doctor only (used when a Doctor role views their own list)
    // also pulls in the doctor's email now, shown as an extra column in the list
    public List<AppointmentListItem> findFiltered(Integer lockedDoctorId, String doctorNameFilter,
            String patientNameFilter, LocalDate dateFilter, Integer appointmentNumberFilter,
            int offset, int limit) throws Exception {

        List<AppointmentListItem> results = new ArrayList<>();

        // joins in patient/doctor names and doctor email so we don't need extra lookups
        StringBuilder sql = new StringBuilder(
                "SELECT a.appointment_id, a.appointment_number, a.appointment_datetime, a.status, "
                + "p.first_name AS patient_first, p.last_name AS patient_last, "
                + "u.first_name AS doctor_first, u.last_name AS doctor_last, u.email AS doctor_email "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN users u ON a.doctor_id = u.user_id "
                + "WHERE 1=1 ");

        // builds up the ? values in order as filters get added
        List<Object> params = new ArrayList<>();

        // doctor role locked to their own only
        if (lockedDoctorId != null) {
            sql.append("AND a.doctor_id = ? ");
            params.add(lockedDoctorId);
        }

        // optional doctor name search
        if (doctorNameFilter != null && !doctorNameFilter.isBlank()) {
            sql.append("AND (u.first_name LIKE ? OR u.last_name LIKE ?) ");
            params.add("%" + doctorNameFilter + "%");
            params.add("%" + doctorNameFilter + "%");
        }

        // optional patient name search
        if (patientNameFilter != null && !patientNameFilter.isBlank()) {
            sql.append("AND (p.first_name LIKE ? OR p.last_name LIKE ?) ");
            params.add("%" + patientNameFilter + "%");
            params.add("%" + patientNameFilter + "%");
        }

        // check if anything was actually searched for
        boolean anyFilterGiven = (doctorNameFilter != null && !doctorNameFilter.isBlank())
                || (patientNameFilter != null && !patientNameFilter.isBlank())
                || dateFilter != null
                || appointmentNumberFilter != null;

        if (dateFilter != null) {
            // exact date picked
            sql.append("AND DATE(a.appointment_datetime) = ? ");
            params.add(java.sql.Date.valueOf(dateFilter));

        } else if (!anyFilterGiven) {
            // nothing searched at all - just show today onwards
            sql.append("AND a.appointment_datetime >= CURRENT_DATE() ");
        }
        // if some other filter was used but no date, show full history for that filter

        // optional appointment number search - can match more than one since numbers repeat per doctor per day
        if (appointmentNumberFilter != null) {
            sql.append("AND a.appointment_number = ? ");
            params.add(appointmentNumberFilter);
        }

        // pagination - limit how many rows and where to start
        sql.append("ORDER BY a.appointment_datetime LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // fill in each ? in the same order we built the list
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            // one display item per row, including the doctor's email now
            while (rs.next()) {
                AppointmentListItem item = new AppointmentListItem(
                        rs.getInt("appointment_id"),
                        rs.getInt("appointment_number"),
                        rs.getTimestamp("appointment_datetime").toLocalDateTime(),
                        rs.getString("status"),
                        rs.getString("patient_first") + " " + rs.getString("patient_last"),
                        rs.getString("doctor_first") + " " + rs.getString("doctor_last"),
                        rs.getString("doctor_email")
                );
                results.add(item);
            }

            return results;
        }
    }

    // same conflict check, but ignores one specific appointment (used when rescheduling that appointment)
    public boolean existsConflictExcluding(int doctorId, LocalDateTime dateTime, int excludeAppointmentId) throws Exception {

        String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_datetime = ? "
                + "AND status != 'CANCELLED' AND appointment_id != ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));
            stmt.setInt(3, excludeAppointmentId);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    // same filters as findFiltered, just counts total matches - used for pagination
    public int countFiltered(Integer lockedDoctorId, String doctorNameFilter,
            String patientNameFilter, LocalDate dateFilter, Integer appointmentNumberFilter) throws Exception {

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN users u ON a.doctor_id = u.user_id "
                + "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // same filter logic as findFiltered, just building a count query instead
        if (lockedDoctorId != null) {
            sql.append("AND a.doctor_id = ? ");
            params.add(lockedDoctorId);
        }
        if (doctorNameFilter != null && !doctorNameFilter.isBlank()) {
            sql.append("AND (u.first_name LIKE ? OR u.last_name LIKE ?) ");
            params.add("%" + doctorNameFilter + "%");
            params.add("%" + doctorNameFilter + "%");
        }
        if (patientNameFilter != null && !patientNameFilter.isBlank()) {
            sql.append("AND (p.first_name LIKE ? OR p.last_name LIKE ?) ");
            params.add("%" + patientNameFilter + "%");
            params.add("%" + patientNameFilter + "%");
        }

        boolean anyFilterGiven = (doctorNameFilter != null && !doctorNameFilter.isBlank())
                || (patientNameFilter != null && !patientNameFilter.isBlank())
                || dateFilter != null
                || appointmentNumberFilter != null;

        if (dateFilter != null) {
            sql.append("AND DATE(a.appointment_datetime) = ? ");
            params.add(java.sql.Date.valueOf(dateFilter));
        } else if (!anyFilterGiven) {
            sql.append("AND a.appointment_datetime >= CURRENT_DATE() ");
        }

        if (appointmentNumberFilter != null) {
            sql.append("AND a.appointment_number = ? ");
            params.add(appointmentNumberFilter);
        }

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            // return the matching count, or zero if none
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }
}