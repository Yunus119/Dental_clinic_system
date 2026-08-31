package com.dentalclinic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private int patientId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String address;
}