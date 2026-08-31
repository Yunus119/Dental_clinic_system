package com.dentalclinic.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    private int appointmentId;
    private int appointmentNumber;
    private String status;   // e.g. "SCHEDULED", "COMPLETED", "CANCELLED"

    // when the appointment is booked for - needed to check doctor conflicts
    private LocalDateTime appointmentDateTime;

    // just storing the ids here, not the whole objects
    // keeps this class simple, DAO handles joining the real data
    private int patientId;
    private int doctorId;
    private int treatmentTypeId;
}