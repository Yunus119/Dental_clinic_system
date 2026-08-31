package com.dentalclinic.model;

// no extra fields or methods, just marks this user as a receptionist
public class Receptionist extends User {

    public Receptionist() {
        super();
    }

    public Receptionist(int userId, String username, String password, String firstName,String lastName, String email) {
        super(userId, username, password, firstName, lastName, email, "RECEPTIONIST");
    }
}