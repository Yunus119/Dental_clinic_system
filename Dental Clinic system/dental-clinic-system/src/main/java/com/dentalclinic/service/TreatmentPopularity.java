package com.dentalclinic.service;

import lombok.Getter;
import lombok.AllArgsConstructor;

// simple holder for one row in the "most requested treatments" report
@Getter
@AllArgsConstructor
public class TreatmentPopularity {

    private String treatmentName;
    private int timesBooked;
}