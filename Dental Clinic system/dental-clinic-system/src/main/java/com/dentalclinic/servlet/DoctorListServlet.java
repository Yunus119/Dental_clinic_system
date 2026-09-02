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

import com.dentalclinic.model.User;
import com.dentalclinic.service.UserService;

@WebServlet("/doctorList")
public class DoctorListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    private static final int PAGE_SIZE = 20;

    // shows the doctor list - admin or receptionist only
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            List<User> doctors = userService.listDoctorsPaginated(page, PAGE_SIZE);
            int totalDoctors = userService.countAllDoctors();
            int totalPages = Math.max((int) Math.ceil((double) totalDoctors / PAGE_SIZE), 1);

            request.setAttribute("doctors", doctors);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            RequestDispatcher dispatcher = request.getRequestDispatcher("doctor_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load doctors", e);
        }
    }

    // handles the search form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdminOrReceptionist(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins and receptionists only");
            return;
        }

        try {
            String name = request.getParameter("searchName");
            List<User> doctors = userService.searchDoctor(name);

            request.setAttribute("doctors", doctors);
            request.setAttribute("isSearchResult", true);
            request.setAttribute("searchedName", name);

            RequestDispatcher dispatcher = request.getRequestDispatcher("doctor_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Search failed", e);
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