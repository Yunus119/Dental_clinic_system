package com.dentalclinic.viewer;

import java.time.LocalDate;
import java.util.List;
import com.dentalclinic.model.Appointment;

public interface IAppointmentViewer {

    // list appointments for a doctor
    List<Appointment> listAppointments(int doctorId);

    // search appointments for a doctor on a specific date
    List<Appointment> searchAppointment(int doctorId, LocalDate date);
}