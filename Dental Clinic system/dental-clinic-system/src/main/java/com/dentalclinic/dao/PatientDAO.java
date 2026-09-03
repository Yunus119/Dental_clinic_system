package com.dentalclinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dentalclinic.model.Patient;
import com.dentalclinic.util.DBConnection;

public class PatientDAO {

    // insert new patient, return it with generated id
    public Patient save(Patient patient) throws Exception {

        String sql = "INSERT INTO patients (first_name, last_name, contact_number, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getAddress());

            stmt.executeUpdate();

            // grab the new id
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                patient.setPatientId(keys.getInt(1));
            }

            return patient;
        }
    }

    // get all patients
    public List<Patient> findAll() throws Exception {

        List<Patient> results = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            // go through each row and turn it into a Patient
            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }
    
	// one page of patients, 20 at a time
	public List<Patient> findAllPaginated(int offset, int limit) throws Exception {

		List<Patient> results = new ArrayList<>();
		String sql = "SELECT * FROM patients ORDER BY last_name LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, limit);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();

			// collect just this page of patients
			while (rs.next()) {
				results.add(mapRow(rs));
			}

			return results;
		}
	}

	// total number of patients - for working out how many pages exist
	public int countAllPatients() throws Exception {

		String sql = "SELECT COUNT(*) AS total FROM patients";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			ResultSet rs = stmt.executeQuery();

			// return the count, or zero if nothing came back
			if (rs.next()) {
				return rs.getInt("total");
			}
			return 0;
		}
	}

    // search patient by name, partial match
    public List<Patient> findByName(String name) throws Exception {

        List<Patient> results = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // wrap the name in % so it matches anywhere in first or last name
            String pattern = "%" + name + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            ResultSet rs = stmt.executeQuery();

            // add every matching patient to the list
            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }

    // update existing patient
    public void update(Patient patient) throws Exception {

        String sql = "UPDATE patients SET first_name = ?, last_name = ?, contact_number = ?, address = ? WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getAddress());
            stmt.setInt(5, patient.getPatientId());

            stmt.executeUpdate();
        }
    }
    
    // find one patient by id
    public Patient findById(int patientId) throws Exception {

        String sql = "SELECT * FROM patients WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);

            ResultSet rs = stmt.executeQuery();

            // return the patient if found, otherwise null
            if (rs.next()) {
                return mapRow(rs);
            }

            return null;
        }
    }

    // turns one row of the result set into a Patient object
    private Patient mapRow(ResultSet rs) throws Exception {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setContactNumber(rs.getString("contact_number"));
        p.setAddress(rs.getString("address"));
        return p;
    }
}