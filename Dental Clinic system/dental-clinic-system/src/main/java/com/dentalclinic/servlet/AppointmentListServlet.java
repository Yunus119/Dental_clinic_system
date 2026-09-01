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
import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.UserService;

@WebServlet("/appointmentList")
public class AppointmentListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();
    private UserService userService = new UserService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        // doctor only ever sees their own list - no doctor picking needed
        if (currentUser.getRole().equals("DOCTOR")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list_search.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // admin/receptionist need to pick a doctor first
        if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list_search.jsp");
            dispatcher.forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("searchDoctorForList".equals(action)) {
                handleSearchDoctor(request, response);

            } else if ("viewAppointments".equals(action)) {
                handleViewAppointments(request, response);

            } else {
                doGet(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Appointment list failed", e);
        }
    }

    // used by admin/receptionist to find a doctor first
    private void handleSearchDoctor(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String name = request.getParameter("doctorName");
        List<User> results = userService.searchDoctor(name);

        request.setAttribute("doctorResults", results);

        RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list_search.jsp");
        dispatcher.forward(request, response);
    }

    // shows the actual appointment list for a date range
    private void handleViewAppointments(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        int doctorId;

        // doctor always sees their own id, never trusts a submitted doctorId
        if (currentUser.getRole().equals("DOCTOR")) {
            doctorId = currentUser.getUserId();
        } else {
            doctorId = Integer.parseInt(request.getParameter("doctorId"));
        }

        LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
        LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));

        List<Appointment> appointments = appointmentService.getAppointmentsForDateRange(doctorId, startDate, endDate);

        request.setAttribute("appointments", appointments);
        request.setAttribute("startDate", startDate.toString());
        request.setAttribute("endDate", endDate.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list.jsp");
        dispatcher.forward(request, response);
    }
}