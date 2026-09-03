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

import com.dentalclinic.model.Patient;
import com.dentalclinic.model.User;
import com.dentalclinic.service.PatientService;

@WebServlet("/patientList")
public class PatientListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PatientService patientService = new PatientService();
    private static final int PAGE_SIZE = 20;

    // shows a page of patients - admin, receptionist, or doctor (all read access)
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect("login");
            return;
        }
        try {
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            List<Patient> patients = patientService.listPatientsPaginated(page, PAGE_SIZE);
            int totalPatients = patientService.countAllPatients();
            int totalPages = (int) Math.ceil((double) totalPatients / PAGE_SIZE);
            request.setAttribute("patients", patients);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            RequestDispatcher dispatcher = request.getRequestDispatcher("patient_list.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Failed to load patients", e);
        }
    }

    // routes to search or create, based on the action parameter
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("createPatient".equals(action)) {
                handleCreatePatient(request, response);
            } else {
                handleSearchPatient(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("Patient list operation failed", e);
        }
    }

    // handles the search form submission
    private void handleSearchPatient(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String name = request.getParameter("searchName");
        List<Patient> patients = patientService.searchPatient(name);

        request.setAttribute("patients", patients);
        request.setAttribute("isSearchResult", true);
        request.setAttribute("searchedName", name);

        RequestDispatcher dispatcher = request.getRequestDispatcher("patient_list.jsp");
        dispatcher.forward(request, response);
    }

    // handles creating a new patient from this page
    private void handleCreatePatient(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String contactNumber = request.getParameter("contactNumber");
        String address = request.getParameter("address");

        try {
            patientService.createPatient(firstName, lastName, contactNumber, address);

            // back to the fresh list so the new patient shows up
            response.sendRedirect("patientList");

        } catch (IllegalStateException e) {
            // validation failed - show the list again with the error
            List<Patient> patients = patientService.listPatientsPaginated(1, PAGE_SIZE);
            int totalPatients = patientService.countAllPatients();
            int totalPages = Math.max((int) Math.ceil((double) totalPatients / PAGE_SIZE), 1);

            request.setAttribute("patients", patients);
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("error", e.getMessage());

            RequestDispatcher dispatcher = request.getRequestDispatcher("patient_list.jsp");
            dispatcher.forward(request, response);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }
}