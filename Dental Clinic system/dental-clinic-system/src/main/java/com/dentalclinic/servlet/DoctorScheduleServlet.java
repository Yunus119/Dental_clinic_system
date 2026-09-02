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

import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.SlotInfo;
import com.dentalclinic.service.UserService;

@WebServlet("/doctorSchedule")
public class DoctorScheduleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();
    private UserService userService = new UserService();

    // shows a read-only schedule grid for one doctor, on a chosen date
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            User doctor = userService.getUserById(doctorId);
            request.setAttribute("doctor", doctor);

            String dateParam = request.getParameter("date");

            // only load the grid once a date has actually been picked
            if (dateParam != null && !dateParam.isBlank()) {
                LocalDate date = LocalDate.parse(dateParam);
                List<SlotInfo> schedule = appointmentService.getDaySchedule(doctorId, date);

                request.setAttribute("schedule", schedule);
                request.setAttribute("selectedDate", dateParam);
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher("doctor_schedule.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load doctor schedule", e);
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