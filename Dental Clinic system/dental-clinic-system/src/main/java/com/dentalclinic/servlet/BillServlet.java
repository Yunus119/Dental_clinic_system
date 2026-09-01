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

import com.dentalclinic.model.Patient;
import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.service.PatientService;
import com.dentalclinic.service.TreatmentService;
import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.Bill;
import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.BillingService;
import com.dentalclinic.service.UserService;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    private AppointmentService appointmentService = new AppointmentService();
    private BillingService billingService = new BillingService();
    private PatientService patientService = new PatientService();
    private TreatmentService treatmentService = new TreatmentService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("bill_search.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("searchDoctorForBill".equals(action)) {
                handleSearchDoctor(request, response);

            } else if ("searchAppointmentsForBill".equals(action)) {
                handleSearchAppointments(request, response);

            } else if ("calculateBill".equals(action)) {
                handleCalculateBill(request, response);

            } else if ("printBill".equals(action)) {
                handlePrintBill(request, response);

            } else {
                doGet(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Bill operation failed", e);
        }
    }

    // step 1 - find doctor
    private void handleSearchDoctor(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String name = request.getParameter("doctorName");
        List<User> results = userService.searchDoctor(name);

        request.setAttribute("doctorResults", results);

        RequestDispatcher dispatcher = request.getRequestDispatcher("bill_search.jsp");
        dispatcher.forward(request, response);
    }

    // step 2 - find that doctor's appointments on a given date
    private void handleSearchAppointments(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        LocalDate date = LocalDate.parse(request.getParameter("appointmentDate"));

        List<Appointment> results = appointmentService.searchAppointment(doctorId, date);

        request.setAttribute("appointmentResults", results);

        RequestDispatcher dispatcher = request.getRequestDispatcher("bill_search.jsp");
        dispatcher.forward(request, response);
    }

 // step 3 - calculate the bill for a chosen appointment
    private void handleCalculateBill(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

        try {
            Bill bill = billingService.calculateBill(appointmentId);

            loadBillDetails(request, bill);

            RequestDispatcher dispatcher = request.getRequestDispatcher("bill_result.jsp");
            dispatcher.forward(request, response);

        } catch (IllegalStateException e) {

            // already billed - just show the existing bill instead of a dead-end error
            if (e.getMessage().equals("This appointment has already been billed")) {

                Bill existingBill = billingService.getBillForAppointment(appointmentId);
                loadBillDetails(request, existingBill);

                RequestDispatcher dispatcher = request.getRequestDispatcher("bill_result.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // any other error (e.g. cancelled appointment) - show it on search page as before
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("bill_search.jsp");
            dispatcher.forward(request, response);
        }
    }

    // step 4 - "print" the bill
    private void handlePrintBill(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

        Bill bill = billingService.getBillForAppointment(appointmentId);

        loadBillDetails(request, bill);
        request.setAttribute("printed", true);

        RequestDispatcher dispatcher = request.getRequestDispatcher("bill_result.jsp");
        dispatcher.forward(request, response);
    }

    // pulls together everything needed to show a full bill: appointment, patient, doctor, treatment, current receptionist
    private void loadBillDetails(HttpServletRequest request, Bill bill) throws Exception {

        Appointment appointment = appointmentService.getAppointmentById(bill.getAppointmentId());
        Patient patient = patientService.getPatientById(appointment.getPatientId());
        User doctor = userService.getUserById(appointment.getDoctorId());
        TreatmentType treatment = treatmentService.getTreatmentTypeById(appointment.getTreatmentTypeId());

        // whoever is currently logged in (the receptionist doing the billing)
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        request.setAttribute("bill", bill);
        request.setAttribute("appointment", appointment);
        request.setAttribute("patient", patient);
        request.setAttribute("doctor", doctor);
        request.setAttribute("treatment", treatment);
        request.setAttribute("receptionist", currentUser);
    }
}