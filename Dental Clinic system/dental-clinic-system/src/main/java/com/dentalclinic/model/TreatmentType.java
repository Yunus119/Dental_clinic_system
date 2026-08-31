package com.dentalclinic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentType {

    private int treatmentTypeId;
    private String name;
    private double cost;
}