package com.dentalclinic.viewer;

import java.util.List;
import com.dentalclinic.model.Patient;

public interface IPatientViewer {

    // list all patients
    List<Patient> listPatients();

    // search patient by name
    List<Patient> searchPatient(String name);
}