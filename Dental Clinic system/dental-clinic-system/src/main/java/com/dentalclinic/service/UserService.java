package com.dentalclinic.service;

import java.util.List;

import com.dentalclinic.dao.UserDAO;
import com.dentalclinic.model.User;
import com.dentalclinic.viewer.IUserViewer;

public class UserService implements IUserViewer {

	private UserDAO userDAO = new UserDAO();

	// check login
	public User login(String username, String password) throws Exception {

		// find user
		User user = userDAO.findByUsername(username);

		// user not found
		if (user == null) {
			return null;
		}

		// wrong password
		if (!user.verifyPassword(password)) {
			return null;
		}

		// login success
		return user;
	}
	
	// combined filtered + paginated user search
	public List<User> searchUsersFiltered(String nameFilter, String roleFilter, int page, int pageSize) throws Exception {
		int offset = (page - 1) * pageSize;
		return userDAO.findFiltered(nameFilter, roleFilter, offset, pageSize);
	}

	// count for the same filters
	public int countUsersFiltered(String nameFilter, String roleFilter) throws Exception {
		return userDAO.countFiltered(nameFilter, roleFilter);
	}

	// search doctor by name
	@Override
	public List<User> searchDoctor(String name) {
		try {
			return userDAO.findByName(name, "DOCTOR");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// get one user by id
	public User getUserById(int userId) throws Exception {
		return userDAO.findById(userId);
	}

	// one page of doctors
	public List<User> listDoctorsPaginated(int page, int pageSize) throws Exception {
		int offset = (page - 1) * pageSize;
		return userDAO.findDoctorsPaginated(offset, pageSize);
	}

	// total number of doctors
	public int countAllDoctors() throws Exception {
		return userDAO.countAllDoctors();
	}

	// creates a new user account - admin only
	public User createUser(String role, String username, String plainPassword,
			String firstName, String lastName, String email) throws Exception {

		// check username isn't already taken
		if (userDAO.existsByUsername(username)) {
			throw new IllegalStateException("Username already exists");
		}

		// basic password check
		if (plainPassword == null || plainPassword.length() < 8) {
			throw new IllegalStateException("Password does not meet requirements");
		}

		// hash the password before it touches the database
		String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

		// build the right subclass based on role
		User newUser = com.dentalclinic.factory.UserFactory.createUser(
				role, 0, username, hashedPassword, firstName, lastName, email);

		// save it and return it with the real id filled in
		return userDAO.save(newUser);
	}

	// get one page of users
	public List<User> listUsersPaginated(int page, int pageSize) throws Exception {
		int offset = (page - 1) * pageSize;
		return userDAO.findAllPaginated(offset, pageSize);
	}

	// total number of users
	public int countAllUsers() throws Exception {
		return userDAO.countAllUsers();
	}

	// search any user by name
	public List<User> searchUsersByName(String name) throws Exception {
		return userDAO.searchUsersByName(name);
	}
	
	// update an existing user
	public void updateUser(User user) throws Exception {
		userDAO.update(user);
	}

	// delete a user - blocks deleting yourself
	public void deleteUser(int userId, int currentAdminId) throws Exception {
		if (userId == currentAdminId) {
			throw new IllegalStateException("You cannot delete your own account");
		}
		userDAO.delete(userId);
	}

	// reset a user's password - same strength check as creating one
	public void resetPassword(int userId, String newPlainPassword) throws Exception {
		if (newPlainPassword == null || newPlainPassword.length() < 8) {
			throw new IllegalStateException("Password does not meet requirements");
		}
		String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(newPlainPassword, org.mindrot.jbcrypt.BCrypt.gensalt());
		userDAO.updatePassword(userId, hashed);
	}
}