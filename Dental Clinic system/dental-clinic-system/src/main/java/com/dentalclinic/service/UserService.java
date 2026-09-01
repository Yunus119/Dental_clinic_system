package com.dentalclinic.service;

import java.util.List;

import com.dentalclinic.dao.UserDAO;
import com.dentalclinic.model.User;
import com.dentalclinic.viewer.IUserViewer;

public class UserService implements IUserViewer {

    private UserDAO userDAO = new UserDAO();

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

    @Override
    public List<User> searchDoctor(String name) {
        try {
            // search doctor by name
            return userDAO.findByName(name, "DOCTOR");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // get one user by id
    public User getUserById(int userId) throws Exception {
        return userDAO.findById(userId);
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
}