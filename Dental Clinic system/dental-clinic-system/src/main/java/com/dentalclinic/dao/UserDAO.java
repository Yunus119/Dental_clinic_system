package com.dentalclinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dentalclinic.factory.UserFactory;
import com.dentalclinic.model.User;
import com.dentalclinic.util.DBConnection;

public class UserDAO {

    // looks up a user by username, returns null if nothing matches
    public User findByUsername(String username) throws Exception {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UserFactory.createUser(
                        rs.getString("role"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                );
            }

            return null;
        }
    }

    // checks if a username is already taken
    public boolean existsByUsername(String username) throws Exception {

        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            return rs.next();
        }
    }

    // inserts a new user and returns it with the generated id filled in
    public User save(User user) throws Exception {

        String sql = "INSERT INTO users (username, password_hash, first_name, last_name, email, role) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(keys.getInt(1));
            }

            return user;
        }
    }

    // searches by partial name match, optionally filtered by role
    public List<User> findByName(String name, String role) throws Exception {

        List<User> results = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE (first_name LIKE ? OR last_name LIKE ?) AND role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + name + "%";

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = UserFactory.createUser(
                        rs.getString("role"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                );
                results.add(user);
            }

            return results;
        }
    }
    
    // find one user by id
    public User findById(int userId) throws Exception {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UserFactory.createUser(
                        rs.getString("role"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                );
            }

            return null;
        }
    }
}