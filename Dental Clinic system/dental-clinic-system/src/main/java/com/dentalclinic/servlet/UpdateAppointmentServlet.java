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
import com.dentalclinic.service.SlotInfo;

@WebServlet("/updateAppointment")
public class UpdateAppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();

    // shows the appointment's current details, with a date picker to start rescheduling
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);

            request.setAttribute("appointment", appointment);

            RequestDispatcher dispatcher = request.getRequestDispatcher("update_appointment.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load appointment", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("loadSchedule".equals(action)) {
                handleLoadSchedule(request, response);

            } else if ("saveReschedule".equals(action)) {
                handleSaveReschedule(request, response);

            } else {
                doGet(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Update appointment failed", e);
        }
    }

    // shows the schedule grid for a chosen new date
    private void handleLoadSchedule(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        LocalDate newDate = LocalDate.parse(request.getParameter("newDate"));

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // exclude this appointment itself so its current slot doesn't show as falsely taken
        List<SlotInfo> schedule = appointmentService.getDayScheduleExcluding(
                appointment.getDoctorId(), newDate, appointmentId);

        request.setAttribute("appointment", appointment);
        request.setAttribute("schedule", schedule);
        request.setAttribute("selectedDate", newDate.toString());

        RequestDispatcher dispatcher = request.getRequestDispatcher("update_appointment.jsp");
        dispatcher.forward(request, response);
    }

    // saves the new slot and status
    private void handleSaveReschedule(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        LocalDate newDate = LocalDate.parse(request.getParameter("newDate"));
        int slotNumber = Integer.parseInt(request.getParameter("slotNumber"));
        String status = request.getParameter("status");

        try {
            appointmentService.rescheduleAppointment(appointmentId, newDate, slotNumber, status);
            response.sendRedirect("appointmentList");

        } catch (IllegalStateException e) {
            // slot taken by someone else - reload the grid with the error
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            List<SlotInfo> schedule = appointmentService.getDayScheduleExcluding(
                    appointment.getDoctorId(), newDate, appointmentId);

            request.setAttribute("appointment", appointment);
            request.setAttribute("schedule", schedule);
            request.setAttribute("selectedDate", newDate.toString());
            request.setAttribute("error", e.getMessage());

            RequestDispatcher dispatcher = request.getRequestDispatcher("update_appointment.jsp");
            dispatcher.forward(request, response);
        }
    }

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