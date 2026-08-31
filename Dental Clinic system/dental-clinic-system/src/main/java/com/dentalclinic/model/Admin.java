package com.dentalclinic.model;

public class Admin extends User {

    public Admin() {
        super();
    }

    // role is hardcoded here so we never forget to set it
    public Admin(int userId, String username, String password, String firstName,String lastName, String email) {
    	super(userId, username, password, firstName, lastName, email, "ADMIN");
    	}

    // only admins can do this
    public void resetPassword() {
        System.out.println("Password reset for user: " + getUsername());
    }
}