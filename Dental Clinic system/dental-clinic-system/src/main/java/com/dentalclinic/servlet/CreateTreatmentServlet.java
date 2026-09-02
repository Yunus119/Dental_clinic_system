package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.User;
import com.dentalclinic.service.TreatmentService;

@WebServlet("/createTreatment")
public class CreateTreatmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private TreatmentService treatmentService = new TreatmentService();

    // shows the create form - admin only
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // only admin allowed here
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("create_treatment.jsp");
        dispatcher.forward(request, response);
    }

    // handles the form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            String name = request.getParameter("name");
            double cost = Double.parseDouble(request.getParameter("cost"));

            // create the new treatment type
            treatmentService.createTreatmentType(name, cost);

            // back to the list to see it
            response.sendRedirect("treatmentList");

        } catch (Exception e) {
            throw new ServletException("Failed to create treatment type", e);
        }
    }

    // checks the logged in user is an admin
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }
}