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
import com.dentalclinic.service.ReportService;
import com.dentalclinic.service.TreatmentPopularity;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReportService reportService = new ReportService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("report.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        String action = request.getParameter("action");

        try {
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));

            if ("downloadCsv".equals(action)) {
                handleDownloadCsv(request, response, startDate, endDate);
                return;
            }

            // show the on-screen report
            int appointmentCount = reportService.getAppointmentCount(startDate, endDate);
            double revenue = reportService.getRevenue(startDate, endDate);
            List<TreatmentPopularity> treatmentPopularity = reportService.getTreatmentPopularity(startDate, endDate);

            request.setAttribute("appointmentCount", appointmentCount);
            request.setAttribute("revenue", revenue);
            request.setAttribute("treatmentPopularity", treatmentPopularity);
            request.setAttribute("startDate", startDate.toString());
            request.setAttribute("endDate", endDate.toString());

            RequestDispatcher dispatcher = request.getRequestDispatcher("report.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Report generation failed", e);
        }
    }

    // streams the CSV back as a downloadable file
    private void handleDownloadCsv(HttpServletRequest request, HttpServletResponse response,
                                     LocalDate startDate, LocalDate endDate) throws Exception {

        byte[] csvBytes = reportService.generateReport(startDate, endDate);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clinic_report.csv\"");
        response.setContentLength(csvBytes.length);
        response.getOutputStream().write(csvBytes);
        response.getOutputStream().flush();
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