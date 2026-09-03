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

			// fill in the username
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();

			// found a match, build the right subclass
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

			// no match found
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

			// true if at least one row came back
			return rs.next();
		}
	}

	// inserts a new user and returns it with the generated id filled in
	public User save(User user) throws Exception {

		String sql = "INSERT INTO users (username, password_hash, first_name, last_name, email, role) " + "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			// fill in all the user details
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, user.getEmail());
			stmt.setString(6, user.getRole());

			// run the insert
			stmt.executeUpdate();

			// grab the new auto generated id
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

			// % means match anything before/after
			String searchPattern = "%" + name + "%";

			stmt.setString(1, searchPattern);
			stmt.setString(2, searchPattern);
			stmt.setString(3, role);

			ResultSet rs = stmt.executeQuery();

			// loop through every matching row, not just the first
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

			// found the user
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

			// no user with that id
			return null;
		}
	}

	// list doctors, limited to a max count - for the initial browse grid
	public List<User> findAllDoctors(int limit) throws Exception {

		List<User> results = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE role = 'DOCTOR' LIMIT ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// how many doctors to return at most
			stmt.setInt(1, limit);

			ResultSet rs = stmt.executeQuery();

			// collect every doctor row into the list
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
	
	// one page of doctors, 20 at a time
	public List<User> findDoctorsPaginated(int offset, int limit) throws Exception {

		List<User> results = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE role = 'DOCTOR' ORDER BY last_name LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, limit);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();

			// collect just this page of doctors
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

	// total number of doctors - for working out how many pages exist
	public int countAllDoctors() throws Exception {

		String sql = "SELECT COUNT(*) AS total FROM users WHERE role = 'DOCTOR'";

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
	
	// list every user - for the admin user list page
	public List<User> findAll() throws Exception {

		List<User> results = new ArrayList<>();
		String sql = "SELECT * FROM users ORDER BY role, last_name";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			ResultSet rs = stmt.executeQuery();

			// collect every user row into the list
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
	
	// get one page of users, 20 at a time
	public List<User> findAllPaginated(int offset, int limit) throws Exception {

		List<User> results = new ArrayList<>();
		String sql = "SELECT * FROM users ORDER BY role, last_name LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, limit);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();

			// collect just this page of users
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

	// total number of users - needed to work out how many pages exist
	public int countAllUsers() throws Exception {

		String sql = "SELECT COUNT(*) AS total FROM users";

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

	// search any user by name, no role filter - for the admin user list search
	public List<User> searchUsersByName(String name) throws Exception {

		List<User> results = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY role, last_name";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			// wrap the name in % so it matches anywhere in first or last name
			String pattern = "%" + name + "%";
			stmt.setString(1, pattern);
			stmt.setString(2, pattern);

			ResultSet rs = stmt.executeQuery();

			// add every matching user to the list
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
	
	// update an existing user's editable details - not username or password
	public void update(User user) throws Exception {

		String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, role = ? WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getRole());
			stmt.setInt(5, user.getUserId());

			stmt.executeUpdate();
		}
	}

	// deletes a user by id
	public void delete(int userId) throws Exception {

		String sql = "DELETE FROM users WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			stmt.executeUpdate();
		}
	}

	// updates just the password hash for one user - used by reset password
	public void updatePassword(int userId, String hashedPassword) throws Exception {

		String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, hashedPassword);
			stmt.setInt(2, userId);
			stmt.executeUpdate();
		}
	}
	
	// combined search: optional name filter, optional role filter, always paginated
	public List<User> findFiltered(String nameFilter, String roleFilter, int offset, int limit) throws Exception {

		List<User> results = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
		List<Object> params = new ArrayList<>();

		// only add the name condition if a name filter was actually given
		if (nameFilter != null && !nameFilter.isBlank()) {
			sql.append("AND (first_name LIKE ? OR last_name LIKE ?) ");
			params.add("%" + nameFilter + "%");
			params.add("%" + nameFilter + "%");
		}

		// only add the role condition if a role filter was actually given
		if (roleFilter != null && !roleFilter.isBlank()) {
			sql.append("AND role = ? ");
			params.add(roleFilter);
		}

		// pagination - limit how many rows and where to start
		sql.append("ORDER BY role, last_name LIMIT ? OFFSET ?");
		params.add(limit);
		params.add(offset);

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			// fill in each ? in the same order we built the list
			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			ResultSet rs = stmt.executeQuery();

			// build a user for every matching row
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

	// same filters, just counts total matches - for pagination
	public int countFiltered(String nameFilter, String roleFilter) throws Exception {

		StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM users WHERE 1=1 ");
		List<Object> params = new ArrayList<>();

		// same filter logic as findFiltered, just building a count query instead
		if (nameFilter != null && !nameFilter.isBlank()) {
			sql.append("AND (first_name LIKE ? OR last_name LIKE ?) ");
			params.add("%" + nameFilter + "%");
			params.add("%" + nameFilter + "%");
		}

		if (roleFilter != null && !roleFilter.isBlank()) {
			sql.append("AND role = ? ");
			params.add(roleFilter);
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