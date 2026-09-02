package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.Patient;
import com.dentalclinic.model.User;
import com.dentalclinic.service.PatientService;

@WebServlet("/updatePatient")
public class UpdatePatientServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PatientService patientService = new PatientService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            Patient patient = patientService.getPatientById(patientId);

            request.setAttribute("patient", patient);

            RequestDispatcher dispatcher = request.getRequestDispatcher("update_patient.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load patient", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            Patient patient = patientService.getPatientById(patientId);

            patient.setFirstName(request.getParameter("firstName"));
            patient.setLastName(request.getParameter("lastName"));
            patient.setContactNumber(request.getParameter("contactNumber"));
            patient.setAddress(request.getParameter("address"));

            patientService.updatePatient(patient);

            response.sendRedirect("patientList");

        } catch (IllegalStateException e) {
            // bad contact number format - reload the patient and show the error
            try {
                int patientId = Integer.parseInt(request.getParameter("patientId"));
                Patient patient = patientService.getPatientById(patientId);
                request.setAttribute("patient", patient);
                request.setAttribute("error", e.getMessage());

                RequestDispatcher dispatcher = request.getRequestDispatcher("update_patient.jsp");
                dispatcher.forward(request, response);

            } catch (Exception ex) {
                throw new ServletException("Failed to reload patient after validation error", ex);
            }

        } catch (Exception e) {
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