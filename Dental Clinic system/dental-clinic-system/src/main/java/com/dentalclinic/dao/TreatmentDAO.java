package com.dentalclinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.util.DBConnection;

public class TreatmentDAO {

    // insert new treatment type, return it with generated id
    public TreatmentType save(TreatmentType treatment) throws Exception {

        String sql = "INSERT INTO treatment_types (name, cost) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, treatment.getName());
            stmt.setDouble(2, treatment.getCost());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                treatment.setTreatmentTypeId(keys.getInt(1));
            }

            return treatment;
        }
    }

    // update existing treatment type
    public void update(TreatmentType treatment) throws Exception {

        String sql = "UPDATE treatment_types SET name = ?, cost = ? WHERE treatment_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getName());
            stmt.setDouble(2, treatment.getCost());
            stmt.setInt(3, treatment.getTreatmentTypeId());

            stmt.executeUpdate();
        }
    }

    // search treatment type by name, partial match
    public List<TreatmentType> findByName(String name) throws Exception {

        List<TreatmentType> results = new ArrayList<>();
        String sql = "SELECT * FROM treatment_types WHERE name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }

    // get all treatment types - useful for a dropdown when booking an appointment
    public List<TreatmentType> findAll() throws Exception {

        List<TreatmentType> results = new ArrayList<>();
        String sql = "SELECT * FROM treatment_types";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapRow(rs));
            }

            return results;
        }
    }
    
    // find one treatment type by its id
    public TreatmentType findById(int treatmentTypeId) throws Exception {

        String sql = "SELECT * FROM treatment_types WHERE treatment_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentTypeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            return null;
        }
    }

    private TreatmentType mapRow(ResultSet rs) throws Exception {
        TreatmentType t = new TreatmentType();
        t.setTreatmentTypeId(rs.getInt("treatment_type_id"));
        t.setName(rs.getString("name"));
        t.setCost(rs.getDouble("cost"));
        return t;
    }
    
	// one page of treatment types, 20 at a time
	public List<TreatmentType> findAllPaginated(int offset, int limit) throws Exception {

		List<TreatmentType> results = new ArrayList<>();
		String sql = "SELECT * FROM treatment_types ORDER BY name LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, limit);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				results.add(mapRow(rs));
			}

			return results;
		}
	}

	// total number of treatment types - for working out how many pages exist
	public int countAllTreatmentTypes() throws Exception {

		String sql = "SELECT COUNT(*) AS total FROM treatment_types";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("total");
			}
			return 0;
		}
	}
}