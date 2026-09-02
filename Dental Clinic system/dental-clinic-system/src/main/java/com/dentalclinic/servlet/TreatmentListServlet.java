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

import com.dentalclinic.model.TreatmentType;
import com.dentalclinic.model.User;
import com.dentalclinic.service.TreatmentService;

@WebServlet("/treatmentList")
public class TreatmentListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private TreatmentService treatmentService = new TreatmentService();

    // shows one page of treatment types - admin only
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // only admin allowed here
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            // work out which page to show, default to page 1
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            // load just this page of treatment types
            List<TreatmentType> treatments = treatmentService.listTreatmentTypesPaginated(page, 20);

            // work out total pages, at least 1 even if the list is empty
            int totalTreatments = treatmentService.countAllTreatmentTypes();
            int totalPages = Math.max((int) Math.ceil((double) totalTreatments / 20), 1);

            request.setAttribute("treatments", treatments);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            RequestDispatcher dispatcher = request.getRequestDispatcher("treatment_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load treatment types", e);
        }
    }

    // handles the search form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // only admin allowed here
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            String name = request.getParameter("searchName");

            // search results aren't paginated, just shown as one list
            List<TreatmentType> treatments = treatmentService.searchTreatmentType(name);

            request.setAttribute("treatments", treatments);
            request.setAttribute("isSearchResult", true);
            request.setAttribute("searchedName", name);

            RequestDispatcher dispatcher = request.getRequestDispatcher("treatment_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Search failed", e);
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