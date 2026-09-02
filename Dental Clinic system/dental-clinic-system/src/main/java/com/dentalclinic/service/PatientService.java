package com.dentalclinic.service;

import java.util.List;

import com.dentalclinic.dao.PatientDAO;
import com.dentalclinic.model.Patient;
import com.dentalclinic.viewer.IPatientViewer;

public class PatientService implements IPatientViewer {

    private PatientDAO patientDAO = new PatientDAO();

	// create new patient
	public Patient createPatient(String firstName, String lastName, String contactNumber, String address) throws Exception {

		validateNames(firstName, lastName);
		validateContactNumber(contactNumber);

		Patient patient = new Patient();
		patient.setFirstName(firstName);
		patient.setLastName(lastName);
		patient.setContactNumber(contactNumber);
		patient.setAddress(address);

		return patientDAO.save(patient);
	}

	// update existing patient
	public void updatePatient(Patient patient) throws Exception {

		validateNames(patient.getFirstName(), patient.getLastName());
		validateContactNumber(patient.getContactNumber());

		patientDAO.update(patient);
	}

	// checks first and last name only contain letters, spaces, hyphens or apostrophes
	private void validateNames(String firstName, String lastName) {

		String namePattern = "^[a-zA-Z\\s'-]+$";

		if (firstName == null || !firstName.matches(namePattern)) {
			throw new IllegalStateException("First name must only contain letters");
		}
		if (lastName == null || !lastName.matches(namePattern)) {
			throw new IllegalStateException("Last name must only contain letters");
		}
	}

	// checks contact number format: 0771234567 or +94771234567
	private void validateContactNumber(String contactNumber) {
		if (contactNumber == null || !contactNumber.matches("^(0\\d{9}|\\+94\\d{9})$")) {
			throw new IllegalStateException("Contact number must be in format 0771234567 or +94771234567");
		}
	}
    
    // get one patient by id
    public Patient getPatientById(int patientId) throws Exception {
        return patientDAO.findById(patientId);
    }
    
	// one page of patients
	public List<Patient> listPatientsPaginated(int page, int pageSize) throws Exception {
		int offset = (page - 1) * pageSize;
		return patientDAO.findAllPaginated(offset, pageSize);
	}

	// total number of patients
	public int countAllPatients() throws Exception {
		return patientDAO.countAllPatients();
	}

    // search patient by name
    @Override
    public List<Patient> searchPatient(String name) {
        try {
            return patientDAO.findByName(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // list all patients
    @Override
    public List<Patient> listPatients() {
        try {
            return patientDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}