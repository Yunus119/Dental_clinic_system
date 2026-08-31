package com.dentalclinic.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.dentalclinic.model.Admin;
import com.dentalclinic.model.User;
import java.util.List;
import com.dentalclinic.model.Doctor;

public class UserDAOTest {

	@Test
	public void testFindByNameReturnsMatchingDoctors() throws Exception {

	    UserDAO dao = new UserDAO();

	    // create a doctor with a distinctive name so we can search for it reliably
	    String uniqueSuffix = String.valueOf(System.currentTimeMillis());
	    String firstName = "Zolt" + uniqueSuffix;   // unlikely to clash with real data

	    Doctor testDoctor = new Doctor(0, "doc_" + uniqueSuffix, "hashedpw",
	            firstName, "Tester", uniqueSuffix + "@clinic.com");

	    dao.save(testDoctor);

	    // now search using just part of the first name
	    List<User> results = dao.findByName("Zolt" + uniqueSuffix, "DOCTOR");

	    // should find exactly the one doctor we just created
	    assertEquals(1, results.size());
	    assertEquals(firstName, results.get(0).getFirstName());
	    assertTrue(results.get(0) instanceof Doctor);
	}

	@Test
	public void testFindByNameReturnsEmptyListWhenNoMatch() throws Exception {

	    UserDAO dao = new UserDAO();

	    List<User> results = dao.findByName("xyz_no_such_name_should_exist_123", "DOCTOR");

	    // no matches should give back an empty list, not null
	    assertNotNull(results);
	    assertTrue(results.isEmpty());
	}
}