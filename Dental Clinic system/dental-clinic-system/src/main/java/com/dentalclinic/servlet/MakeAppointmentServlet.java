package com.dentalclinic.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;

import com.dentalclinic.model.Patient;
import com.dentalclinic.service.PatientService;
import com.dentalclinic.model.User;
import com.dentalclinic.service.UserService;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.TreatmentService;
import com.dentalclinic.service.SlotInfo;

@WebServlet("/makeAppointment")
public class MakeAppointmentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private PatientService patientService = new PatientService();
	private UserService userService = new UserService();
	private TreatmentService treatmentService = new TreatmentService();
	private AppointmentService appointmentService = new AppointmentService();


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// admin or receptionist only
		if (!isAdminOrReceptionist(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
			return;
		}

		// shows the first step - patient search
		RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step1.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// admin or receptionist only
		if (!isAdminOrReceptionist(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
			return;
		}

		String action = request.getParameter("action");

		try {
			if ("searchPatient".equals(action)) {
				handleSearchPatient(request, response);

			} else if ("createPatient".equals(action)) {
				handleCreatePatient(request, response);

			} else if ("selectPatient".equals(action)) {
				handleSelectPatient(request, response);

			} else if ("searchDoctor".equals(action)) {
				handleSearchDoctor(request, response);

			} else if ("selectDoctor".equals(action)) {
				handleSelectDoctor(request, response);
			} else if ("bookAppointment".equals(action)) {
				handleBookAppointment(request, response);
			} else if ("loadSchedule".equals(action)) {
			    handleLoadSchedule(request, response);

			} else {
				// unknown action, just show step 1 again
				doGet(request, response);
			}

		} catch (Exception e) {
			throw new ServletException("Make appointment failed", e);
		}
	}

	// only admin or receptionist can access this
	private boolean isAdminOrReceptionist(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}
		User currentUser = (User) session.getAttribute("currentUser");
		return currentUser != null &&
				(currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST"));
	}

	// handles the search form submission
	private void handleSearchPatient(HttpServletRequest request, HttpServletResponse response)
			throws Exception, ServletException, IOException {

		String name = request.getParameter("patientName");

		List<Patient> results = patientService.searchPatient(name);

		request.setAttribute("patientResults", results);
		request.setAttribute("searchedName", name);

		RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step1.jsp");
		dispatcher.forward(request, response);
	}

	// handles creating a brand new patient when search found nobody
	private void handleCreatePatient(HttpServletRequest request, HttpServletResponse response)
			throws Exception, ServletException, IOException {

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String contactNumber = request.getParameter("contactNumber");
		String address = request.getParameter("address");

		try {
			Patient newPatient = patientService.createPatient(firstName, lastName, contactNumber, address);

			HttpSession session = request.getSession();
			session.setAttribute("selectedPatientId", newPatient.getPatientId());

			// move on to step 2 - doctor search
			RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step2.jsp");
			dispatcher.forward(request, response);

		} catch (IllegalStateException e) {
			request.setAttribute("error", e.getMessage());
			RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step1.jsp");
			dispatcher.forward(request, response);
		}
	}

	// handles picking one of the search results
	private void handleSelectPatient(HttpServletRequest request, HttpServletResponse response)
			throws Exception, ServletException, IOException {

		int patientId = Integer.parseInt(request.getParameter("patientId"));

		HttpSession session = request.getSession();
		session.setAttribute("selectedPatientId", patientId);

		// move on to step 2 - doctor search
		RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step2.jsp");
		dispatcher.forward(request, response);
	}

	// handles the doctor search form submission
	private void handleSearchDoctor(HttpServletRequest request, HttpServletResponse response)
			throws Exception, ServletException, IOException {

		String name = request.getParameter("doctorName");

		List<User> results = userService.searchDoctor(name);

		request.setAttribute("doctorResults", results);
		request.setAttribute("searchedDoctorName", name);

		RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step2.jsp");
		dispatcher.forward(request, response);
	}

	// handles picking a doctor from the search results
	// handles picking a doctor from the search results
	private void handleSelectDoctor(HttpServletRequest request, HttpServletResponse response)
	        throws Exception, ServletException, IOException {

	    int doctorId = Integer.parseInt(request.getParameter("doctorId"));

	    HttpSession session = request.getSession();
	    session.setAttribute("selectedDoctorId", doctorId);

	    RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
	    dispatcher.forward(request, response);
	}
	
	// handles loading the schedule grid for a chosen date
	private void handleLoadSchedule(HttpServletRequest request, HttpServletResponse response)
	        throws Exception, ServletException, IOException {

	    HttpSession session = request.getSession();
	    Integer doctorId = (Integer) session.getAttribute("selectedDoctorId");

	    if (doctorId == null) {
	        response.sendRedirect("makeAppointment");
	        return;
	    }

	    LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));

	    List<SlotInfo> schedule = appointmentService.getDaySchedule(doctorId, date);
	    List<TreatmentType> treatmentTypes = treatmentService.listTreatmentTypes();

	    request.setAttribute("schedule", schedule);
	    request.setAttribute("treatmentTypes", treatmentTypes);
	    request.setAttribute("selectedDate", date.toString());

	    RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
	    dispatcher.forward(request, response);
	}

	// handles the final appointment booking submission
	private void handleBookAppointment(HttpServletRequest request, HttpServletResponse response)
	        throws Exception, ServletException, IOException {

	    HttpSession session = request.getSession();
	    Integer patientId = (Integer) session.getAttribute("selectedPatientId");
	    Integer doctorId = (Integer) session.getAttribute("selectedDoctorId");

	    if (patientId == null || doctorId == null) {
	        response.sendRedirect("makeAppointment");
	        return;
	    }

	    int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));
	    int slotNumber = Integer.parseInt(request.getParameter("slotNumber"));
	    LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));

	    try {
	        Appointment booked = appointmentService.makeAppointment(patientId, doctorId, treatmentTypeId, date, slotNumber);

	        session.removeAttribute("selectedPatientId");
	        session.removeAttribute("selectedDoctorId");

	        request.setAttribute("bookedAppointment", booked);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_success.jsp");
	        dispatcher.forward(request, response);

	    } catch (IllegalStateException e) {
	        // slot got taken by someone else between viewing and booking
	        request.setAttribute("error", e.getMessage());

	        List<SlotInfo> schedule = appointmentService.getDaySchedule(doctorId, date);
	        List<TreatmentType> treatmentTypes = treatmentService.listTreatmentTypes();
	        request.setAttribute("schedule", schedule);
	        request.setAttribute("treatmentTypes", treatmentTypes);
	        request.setAttribute("selectedDate", date.toString());

	        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
	        dispatcher.forward(request, response);
	    }
	}
}