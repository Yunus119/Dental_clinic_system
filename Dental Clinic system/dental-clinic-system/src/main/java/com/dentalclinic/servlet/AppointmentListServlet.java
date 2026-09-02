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
import com.dentalclinic.service.AppointmentListItem;
import com.dentalclinic.service.AppointmentService;

@WebServlet("/appointmentList")
public class AppointmentListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();
    private static final int PAGE_SIZE = 20;

    // shows the appointment list - everything lives in the URL as query params
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        try {
            // doctor is always locked to their own appointments
            Integer lockedDoctorId = currentUser.getRole().equals("DOCTOR") ? currentUser.getUserId() : null;

            // read whichever filters were given - all optional
            String doctorNameFilter = request.getParameter("doctorName");
            String patientNameFilter = request.getParameter("patientName");
            String dateParam = request.getParameter("date");
            String numberParam = request.getParameter("appointmentNumber");
            String pageParam = request.getParameter("page");

            LocalDate dateFilter = (dateParam != null && !dateParam.isBlank()) ? LocalDate.parse(dateParam) : null;
            Integer appointmentNumberFilter = (numberParam != null && !numberParam.isBlank())
                    ? Integer.parseInt(numberParam) : null;
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            List<AppointmentListItem> appointments = appointmentService.searchAppointmentsFiltered(
                    lockedDoctorId, doctorNameFilter, patientNameFilter, dateFilter,
                    appointmentNumberFilter, page, PAGE_SIZE);

            int totalResults = appointmentService.countAppointmentsFiltered(
                    lockedDoctorId, doctorNameFilter, patientNameFilter, dateFilter, appointmentNumberFilter);
            int totalPages = Math.max((int) Math.ceil((double) totalResults / PAGE_SIZE), 1);

            request.setAttribute("appointments", appointments);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("isDoctor", currentUser.getRole().equals("DOCTOR"));
            request.setAttribute("doctorNameFilter", doctorNameFilter);
            request.setAttribute("patientNameFilter", patientNameFilter);
            request.setAttribute("dateFilter", dateParam);
            request.setAttribute("appointmentNumberFilter", numberParam);

            RequestDispatcher dispatcher = request.getRequestDispatcher("appointment_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load appointments", e);
        }
    }
}