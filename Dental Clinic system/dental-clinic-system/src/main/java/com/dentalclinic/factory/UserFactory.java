package com.dentalclinic.factory;
import com.dentalclinic.model.Admin;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.Receptionist;
import com.dentalclinic.model.User;
public class UserFactory {
    // takes the role string and builds the right subclass
    public static User createUser(String role, int userId, String username, String password,String firstName, String lastName, String email) 
    {
        // Create an Admin if the role matches
        if (role.equals("ADMIN")) {
            return new Admin(userId, username, password, firstName, lastName, email);
        } 
        // Create a Doctor if the role matches
        else if (role.equals("DOCTOR")) 
        {
            return new Doctor(userId, username, password, firstName, lastName, email);
        } else if (role.equals("RECEPTIONIST")) {
            // Create a Receptionist if the role matches
            return new Receptionist(userId, username, password, firstName, lastName, email);
        } else {
            // Role does not match any known type, so reject it
            throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}