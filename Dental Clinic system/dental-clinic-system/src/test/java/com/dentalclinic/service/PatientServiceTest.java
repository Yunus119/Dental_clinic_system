package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import com.dentalclinic.model.Patient;

public class PatientServiceTest {
	
	// generates a random letters-only string, since patient names can no longer contain digits
	private static String randomLetters(int length) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		java.util.Random rnd = new java.util.Random();
		for (int i = 0; i < length; i++) {
			sb.append(letters.charAt(rnd.nextInt(letters.length())));
		}
		return sb.toString();
	}

    @Test
    public void testCreateAndSearchPatient() throws Exception {

        PatientService service = new PatientService();

        // unique-ish name so this test doesn't collide with real data
        String uniqueLastName = "Zolt" + randomLetters(8);;

        Patient created = service.createPatient("Test", uniqueLastName, "0771234567", "123 Test Street");

        // should have a real database-generated id now
        assertTrue(created.getPatientId() > 0);

        // search should find it back
        List<Patient> results = service.searchPatient(uniqueLastName);

        assertEquals(1, results.size());
        assertEquals(uniqueLastName, results.get(0).getLastName());
    }

    @Test
    public void testUpdatePatient() throws Exception {

        PatientService service = new PatientService();

        String uniqueLastName = "Zolt" + randomLetters(8);
        Patient created = service.createPatient("Test", uniqueLastName, "0771234567", "Old Address");

        // change the address and save it
        created.setAddress("New Address");
        service.updatePatient(created);

        // search again and confirm the address actually changed in the database
        List<Patient> results = service.searchPatient(uniqueLastName);

        assertEquals("New Address", results.get(0).getAddress());
    }

    @Test
    public void testListPatientsReturnsSomething() throws Exception {

        PatientService service = new PatientService();

        List<Patient> allPatients = service.listPatients();

        // just confirming this doesn't crash and returns a real list
        assertNotNull(allPatients);
    }

    @Test
    public void testSearchWithNoMatchReturnsEmptyList() throws Exception {

        PatientService service = new PatientService();

        List<Patient> results = service.searchPatient("xyz_no_such_patient_name_123");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}