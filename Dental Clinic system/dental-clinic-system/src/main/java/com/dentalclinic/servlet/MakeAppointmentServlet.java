package com.dentalclinic.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.PatientService;
import com.dentalclinic.service.SlotInfo;
import com.dentalclinic.service.TreatmentService;
import com.dentalclinic.service.UserService;

@WebServlet("/makeAppointment")
public class MakeAppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PatientService patientService = new PatientService();
    private UserService userService = new UserService();
    private TreatmentService treatmentService = new TreatmentService();
    private AppointmentService appointmentService = new AppointmentService();

    // shows step 1 with first 20 doctors when page first loads
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            List<User> doctors = userService.listDoctorsPaginated(page, 20);
            int totalDoctors = userService.countAllDoctors();
            int totalPages = (int) Math.ceil((double) totalDoctors / 20);

            request.setAttribute("doctorResults", doctors);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step1.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load doctors", e);
        }
    }

    // routes every form submission to the right handler based on action
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // only admin or receptionist allowed
        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "searchDoctor":
                    handleSearchDoctor(request, response);
                    break;
                case "selectDoctor":
                    handleSelectDoctor(request, response);
                    break;
                case "loadSchedule":
                    handleLoadSchedule(request, response);
                    break;
                case "selectSlot":
                    handleSelectSlot(request, response);
                    break;
                case "searchPatient":
                    handleSearchPatient(request, response);
                    break;
                case "createPatientAndBook":
                    handleCreatePatientAndBook(request, response);
                    break;
                case "selectPatientAndBook":
                    handleSelectPatientAndBook(request, response);
                    break;
                default:
                    doGet(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Make appointment failed", e);
        }
    }

    // checks the logged in user is admin or receptionist
    private boolean isAdminOrReceptionist(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null &&
                (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST"));
    }

    // step 1 - narrows the doctor grid when receptionist types a name
    private void handleSearchDoctor(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String name = request.getParameter("doctorName");
        List<User> results = userService.searchDoctor(name);

        request.setAttribute("doctorResults", results);

        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step1.jsp");
        dispatcher.forward(request, response);
    }

    // step 1 done - doctor picked, save it and move to step 2
    private void handleSelectDoctor(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int doctorId = Integer.parseInt(request.getParameter("doctorId"));

        HttpSession session = request.getSession();
        session.setAttribute("selectedDoctorId", doctorId);

        // load treatment types for the dropdown on step 2
        List<TreatmentType> treatmentTypes = treatmentService.listTreatmentTypes();
        request.setAttribute("treatmentTypes", treatmentTypes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step2.jsp");
        dispatcher.forward(request, response);
    }

    // step 2 - shows the schedule grid once a date is picked
    private void handleLoadSchedule(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        HttpSession session = request.getSession();
        Integer doctorId = (Integer) session.getAttribute("selectedDoctorId");

        // no doctor in session, something went wrong, start over
        if (doctorId == null) {
            response.sendRedirect("makeAppointment");
            return;
        }

        LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));

        // build the grid showing which slots are free or taken
        List<SlotInfo> schedule = appointmentService.getDaySchedule(doctorId, date);
        List<TreatmentType> treatmentTypes = treatmentService.listTreatmentTypes();

        request.setAttribute("schedule", schedule);
        request.setAttribute("treatmentTypes", treatmentTypes);
        request.setAttribute("selectedDate", date.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step2.jsp");
        dispatcher.forward(request, response);
    }

    // step 2 done - slot and treatment picked, save both, move to step 3
    private void handleSelectSlot(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        HttpSession session = request.getSession();
        Integer doctorId = (Integer) session.getAttribute("selectedDoctorId");

        if (doctorId == null) {
            response.sendRedirect("makeAppointment");
            return;
        }

        int slotNumber = Integer.parseInt(request.getParameter("slotNumber"));
        int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));
        LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));

        session.setAttribute("selectedSlotNumber", slotNumber);
        session.setAttribute("selectedTreatmentTypeId", treatmentTypeId);
        session.setAttribute("selectedDate", date.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
        dispatcher.forward(request, response);
    }

    // step 3 - searches for a patient by name
    private void handleSearchPatient(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String name = request.getParameter("patientName");
        List<Patient> results = patientService.searchPatient(name);

        request.setAttribute("patientResults", results);
        request.setAttribute("searchedName", name);

        RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
        dispatcher.forward(request, response);
    }

    // step 3 - patient not found, create a new one from the popup form, then book right away
    private void handleCreatePatientAndBook(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String contactNumber = request.getParameter("contactNumber");
        String address = request.getParameter("address");

        try {
            // create the patient first
            Patient newPatient = patientService.createPatient(firstName, lastName, contactNumber, address);

            // then finish the booking using this new patient
            completeBooking(request, response, newPatient.getPatientId());

        } catch (IllegalStateException e) {
            // bad contact number or similar - show the error, stay on step 3
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
            dispatcher.forward(request, response);
        }
    }

    // step 3 - existing patient picked from search results, book right away
    private void handleSelectPatientAndBook(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int patientId = Integer.parseInt(request.getParameter("patientId"));
        completeBooking(request, response, patientId);
    }

    // shared final step - pulls doctor, date, slot, treatment from session and books the appointment
    private void completeBooking(HttpServletRequest request, HttpServletResponse response, int patientId)
            throws Exception, ServletException, IOException {

        HttpSession session = request.getSession();
        Integer doctorId = (Integer) session.getAttribute("selectedDoctorId");
        Integer slotNumber = (Integer) session.getAttribute("selectedSlotNumber");
        Integer treatmentTypeId = (Integer) session.getAttribute("selectedTreatmentTypeId");
        String dateStr = (String) session.getAttribute("selectedDate");

        // something is missing from session, start over
        if (doctorId == null || slotNumber == null || treatmentTypeId == null || dateStr == null) {
            response.sendRedirect("makeAppointment");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);

        try {
            // actually book the appointment
            Appointment booked = appointmentService.makeAppointment(patientId, doctorId, treatmentTypeId, date, slotNumber);

            // clear session now booking is done
            session.removeAttribute("selectedDoctorId");
            session.removeAttribute("selectedSlotNumber");
            session.removeAttribute("selectedTreatmentTypeId");
            session.removeAttribute("selectedDate");

            request.setAttribute("bookedAppointment", booked);

            RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_success.jsp");
            dispatcher.forward(request, response);

        } catch (IllegalStateException e) {
            // slot got taken by someone else in the meantime - go back to step 3 with the error
            request.setAttribute("error", e.getMessage());

            RequestDispatcher dispatcher = request.getRequestDispatcher("make_appointment_step3.jsp");
            dispatcher.forward(request, response);
        }
    }
}