package com.dentalclinic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Lombok generates all the getters and setters for us at compile time
// no need to write them by hand
@Getter
@Setter
@NoArgsConstructor      // gives us an empty constructor: new User()
@AllArgsConstructor     // gives us a constructor with all fields
public class User {

    private int userId;
    private String username;
    private String password;   // stores the BCrypt hash, never plain text

    // protected so Admin /Receptionist /Doctor can access these directly
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String role;

    // just a placeholder for now, real login logic lives in UserService
    public void login() {
        System.out.println(username + " logged in");
    }

    public void logout() {
        System.out.println(username + " logged out");
    }

    // compares the plain text attempt against the stored hash
    public boolean verifyPassword(String attempt) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(attempt, this.password);
    }
}