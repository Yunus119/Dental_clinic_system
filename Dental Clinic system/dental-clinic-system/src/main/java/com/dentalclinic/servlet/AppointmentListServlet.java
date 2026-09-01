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

import com.dentalclinic.model.Appointment;
import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;

@WebServlet("/appointmentList")
public class AppointmentListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // doctor only - filter already confirmed someone is logged in
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (!currentUser.getRole().equals("DOCTOR")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Doctors only");
            return;
        }

        try {
            // doctor only ever sees their own appointments
            List<Appointment> appointments = appointmentService.listAppointments(currentUser.getUserId());

            request.setAttribute("appointments", appointments);

            RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load appointments", e);
        }
    }
}