package com.dentalclinic.service;

import java.util.List;

import com.dentalclinic.dao.TreatmentDAO;
import com.dentalclinic.model.TreatmentType;

public class TreatmentService {

    private TreatmentDAO treatmentDAO = new TreatmentDAO();

    // create new treatment type
    public TreatmentType createTreatmentType(String name, double cost) throws Exception {
        TreatmentType treatment = new TreatmentType();
        treatment.setName(name);
        treatment.setCost(cost);
        return treatmentDAO.save(treatment);
    }

    // update existing treatment type
    public void updateTreatmentType(TreatmentType treatment) throws Exception {
        treatmentDAO.update(treatment);
    }

    // search treatment type by name
    public List<TreatmentType> searchTreatmentType(String name) throws Exception {
        return treatmentDAO.findByName(name);
    }

    // get all treatment types
    public List<TreatmentType> listTreatmentTypes() throws Exception {
        return treatmentDAO.findAll();
    }
    
    // get one treatment type by id
    public TreatmentType getTreatmentTypeById(int treatmentTypeId) throws Exception {
        return treatmentDAO.findById(treatmentTypeId);
    }
}