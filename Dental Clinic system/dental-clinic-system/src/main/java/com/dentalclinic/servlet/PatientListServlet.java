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

    // handles the search form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect("login");
            return;
        }

        try {
            String name = request.getParameter("searchName");
            List<Patient> patients = patientService.searchPatient(name);

            request.setAttribute("patients", patients);
            request.setAttribute("isSearchResult", true);
            request.setAttribute("searchedName", name);

            RequestDispatcher dispatcher = request.getRequestDispatcher("patient_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Search failed", e);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }
}