package com.dentalclinic.factory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.dentalclinic.model.Admin;
import com.dentalclinic.model.Doctor;
import com.dentalclinic.model.User;

public class UserFactoryTest {

    @Test
    public void testCreateAdmin() {
        User user = UserFactory.createUser("ADMIN", 1, "jdoe", "hashedpw",
                "John", "Doe", "john@clinic.com");

        // check it actually built an Admin, not just any User
        assertTrue(user instanceof Admin);

        // check the role got set correctly
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void testCreateDoctor() {
        User user = UserFactory.createUser("DOCTOR", 2, "asmith", "hashedpw",
                "Alice", "Smith", "alice@clinic.com");

        assertTrue(user instanceof Doctor);
        assertEquals("DOCTOR", user.getRole());
    }

    @Test
    public void testInvalidRoleThrowsException() {
        // an unknown role should throw an error, not silently create something wrong
        assertThrows(IllegalArgumentException.class, () -> {
            UserFactory.createUser("NURSE", 3, "x", "x", "x", "x", "x");
        });
    }
}