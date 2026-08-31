package com.dentalclinic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    private int billId;
    private double amount;

    // links back to the appointment this bill belongs to
    private int appointmentId;
}