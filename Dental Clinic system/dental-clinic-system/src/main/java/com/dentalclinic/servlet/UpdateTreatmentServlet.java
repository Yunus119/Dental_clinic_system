package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.model.User;
import com.dentalclinic.service.TreatmentService;

@WebServlet("/updateTreatment")
public class UpdateTreatmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private TreatmentService treatmentService = new TreatmentService();

    // shows the edit form, pre-filled with the current name and cost
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));

            // load the existing treatment type to pre-fill the form
            TreatmentType treatment = treatmentService.getTreatmentTypeById(treatmentTypeId);
            request.setAttribute("treatment", treatment);

            RequestDispatcher dispatcher = request.getRequestDispatcher("update_treatment.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load treatment type", e);
        }
    }

    // saves the edited name/cost
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int treatmentTypeId = Integer.parseInt(request.getParameter("treatmentTypeId"));

            // load it, apply the changes, save it back
            TreatmentType treatment = treatmentService.getTreatmentTypeById(treatmentTypeId);
            treatment.setName(request.getParameter("name"));
            treatment.setCost(Double.parseDouble(request.getParameter("cost")));

            treatmentService.updateTreatmentType(treatment);

            response.sendRedirect("treatmentList");

        } catch (Exception e) {
            throw new ServletException("Failed to update treatment type", e);
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