package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;

@WebServlet("/cancelAppointment")
public class CancelAppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();

    // cancels an appointment - admin or receptionist only
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // only admin or receptionist allowed
        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

            // flips status to CANCELLED, doesn't delete the row
            appointmentService.cancelAppointment(appointmentId);

            // back to the appointment list to see the change
            response.sendRedirect("appointmentList");

        } catch (Exception e) {
            throw new ServletException("Failed to cancel appointment", e);
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
}