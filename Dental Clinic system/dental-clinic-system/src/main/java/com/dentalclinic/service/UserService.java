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
}