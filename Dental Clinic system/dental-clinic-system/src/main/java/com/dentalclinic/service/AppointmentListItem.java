package com.dentalclinic.service;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;

// display friendly holder for one row in the appointment list - includes names, not just ids
@Getter
@AllArgsConstructor
public class AppointmentListItem {

    private int appointmentId;
    private int appointmentNumber;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String patientName;
    private String doctorName;
    private String doctorEmail;
}