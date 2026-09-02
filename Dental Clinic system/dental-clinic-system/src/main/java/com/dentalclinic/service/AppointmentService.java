package com.dentalclinic.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import com.dentalclinic.dao.AppointmentDAO;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.util.TimeSlotUtil;
import com.dentalclinic.viewer.IAppointmentViewer;

public class AppointmentService implements IAppointmentViewer {

	private AppointmentDAO appointmentDAO = new AppointmentDAO();

	// book a new appointment - patient picks a date and a slot number (1-based)
	public Appointment makeAppointment(int patientId, int doctorId, int treatmentTypeId, LocalDate date, int slotNumber) throws Exception {

		// convert the slot number into an actual date+time
		LocalDateTime dateTime = TimeSlotUtil.getDateTimeForSlot(date, slotNumber);

		// reject if the doctor is already booked at this time
		if (appointmentDAO.existsConflict(doctorId, dateTime)) {
			throw new IllegalStateException("This slot is already booked");
		}

		Appointment appointment = new Appointment();
		appointment.setAppointmentNumber(slotNumber);
		appointment.setStatus("SCHEDULED");
		appointment.setAppointmentDateTime(dateTime);
		appointment.setPatientId(patientId);
		appointment.setDoctorId(doctorId);
		appointment.setTreatmentTypeId(treatmentTypeId);

		return appointmentDAO.save(appointment);
	}

	// builds the full schedule grid for a doctor on a given date
	// each slot marked available or taken
	public List<SlotInfo> getDaySchedule(int doctorId, LocalDate date) throws Exception {

		List<Appointment> existing = appointmentDAO.findByDoctorAndDate(doctorId, date);

		// collect which slot numbers are taken (ignore cancelled ones)
		List<Integer> takenSlots = new ArrayList<>();
		for (Appointment a : existing) {
			if (!a.getStatus().equals("CANCELLED")) {
				takenSlots.add(a.getAppointmentNumber());
			}
		}

		List<LocalTime> allTimes = TimeSlotUtil.getAllSlotTimes();

		List<SlotInfo> schedule = new ArrayList<>();
		for (int i = 0; i < allTimes.size(); i++) {
			int slotNumber = i + 1;
			boolean available = !takenSlots.contains(slotNumber);
			schedule.add(new SlotInfo(slotNumber, allTimes.get(i), available));
		}

		return schedule;
	}
	
	// same as getDaySchedule, but ignores one appointment (used when rescheduling it)
	public List<SlotInfo> getDayScheduleExcluding(int doctorId, LocalDate date, int excludeAppointmentId) throws Exception {

		List<Appointment> existing = appointmentDAO.findByDoctorAndDate(doctorId, date);

		List<Integer> takenSlots = new ArrayList<>();
		for (Appointment a : existing) {
			if (!a.getStatus().equals("CANCELLED") && a.getAppointmentId() != excludeAppointmentId) {
				takenSlots.add(a.getAppointmentNumber());
			}
		}

		List<LocalTime> allTimes = TimeSlotUtil.getAllSlotTimes();

		List<SlotInfo> schedule = new ArrayList<>();
		for (int i = 0; i < allTimes.size(); i++) {
			int slotNumber = i + 1;
			boolean available = !takenSlots.contains(slotNumber);
			schedule.add(new SlotInfo(slotNumber, allTimes.get(i), available));
		}

		return schedule;
	}

	// reschedules an appointment to a new date/slot, and updates its status
	public Appointment rescheduleAppointment(int appointmentId, LocalDate newDate, int newSlotNumber, String newStatus) throws Exception {

		Appointment appointment = appointmentDAO.findById(appointmentId);

		LocalDateTime newDateTime = TimeSlotUtil.getDateTimeForSlot(newDate, newSlotNumber);

		// only check for conflict if the slot is actually changing
		boolean slotChanged = !appointment.getAppointmentDateTime().equals(newDateTime);

		if (slotChanged && appointmentDAO.existsConflictExcluding(appointment.getDoctorId(), newDateTime, appointmentId)) {
			throw new IllegalStateException("This slot is already booked");
		}

		appointment.setAppointmentNumber(newSlotNumber);
		appointment.setAppointmentDateTime(newDateTime);
		appointment.setStatus(newStatus);

		appointmentDAO.update(appointment);
		return appointment;
	}

	// update an existing appointment
	public void updateAppointment(Appointment appointment) throws Exception {
		appointmentDAO.update(appointment);
	}

	// cancel an appointment
	public void cancelAppointment(int appointmentId) throws Exception {
		appointmentDAO.cancel(appointmentId);
	}

	// get one appointment by id
	public Appointment getAppointmentById(int appointmentId) throws Exception {
		return appointmentDAO.findById(appointmentId);
	}
	
	// filtered, paginated appointment search
	public List<AppointmentListItem> searchAppointmentsFiltered(Integer lockedDoctorId, String doctorNameFilter,
			String patientNameFilter, LocalDate dateFilter, Integer appointmentNumberFilter,
			int page, int pageSize) throws Exception {
		int offset = (page - 1) * pageSize;
		return appointmentDAO.findFiltered(lockedDoctorId, doctorNameFilter, patientNameFilter,
				dateFilter, appointmentNumberFilter, offset, pageSize);
	}

	// count for the same filters - needed for pagination
	public int countAppointmentsFiltered(Integer lockedDoctorId, String doctorNameFilter,
			String patientNameFilter, LocalDate dateFilter, Integer appointmentNumberFilter) throws Exception {
		return appointmentDAO.countFiltered(lockedDoctorId, doctorNameFilter, patientNameFilter,
				dateFilter, appointmentNumberFilter);
	}

	// list appointments for a doctor
	@Override
	public List<Appointment> listAppointments(int doctorId) {
		try {
			return appointmentDAO.findByDoctor(doctorId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// search appointments for a doctor on a specific date
	@Override
	public List<Appointment> searchAppointment(int doctorId, LocalDate date) {
		try {
			return appointmentDAO.findByDoctorAndDate(doctorId, date);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}