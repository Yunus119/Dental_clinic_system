package com.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import com.dentalclinic.model.TreatmentType;

public class TreatmentServiceTest {

    @Test
    public void testCreateAndSearchTreatmentType() throws Exception {

        TreatmentService service = new TreatmentService();

        String uniqueName = "TestTreatment" + System.currentTimeMillis();

        TreatmentType created = service.createTreatmentType(uniqueName, 150.00);

        // should have a real id now
        assertTrue(created.getTreatmentTypeId() > 0);

        // search should find it
        List<TreatmentType> results = service.searchTreatmentType(uniqueName);

        assertEquals(1, results.size());
        assertEquals(uniqueName, results.get(0).getName());
        assertEquals(150.00, results.get(0).getCost());
    }
}