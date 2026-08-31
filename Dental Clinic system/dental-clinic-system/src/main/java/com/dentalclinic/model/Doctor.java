package com.dentalclinic.model;

// no extra fields or methods, just marks this user as a doctor
public class Doctor extends User {

    public Doctor() {
        super();
    }

    public Doctor(int userId, String username, String password, String firstName,String lastName, String email) {
        super(userId, username, password, firstName, lastName, email, "DOCTOR");
    }
}