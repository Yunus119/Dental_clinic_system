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

@WebServlet("/userList")
public class UserListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    private static final int PAGE_SIZE = 20;

    // shows page 1 by default, or a specific page if ?page=N is given
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            List<User> users = userService.listUsersPaginated(page, PAGE_SIZE);
            int totalUsers = userService.countAllUsers();
            int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

            request.setAttribute("users", users);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            RequestDispatcher dispatcher = request.getRequestDispatcher("user_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load users", e);
        }
    }

    // handles the search form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            String name = request.getParameter("searchName");
            List<User> users = userService.searchUsersByName(name);

            // search results aren't paginated - just shown as one list
            request.setAttribute("users", users);
            request.setAttribute("isSearchResult", true);
            request.setAttribute("searchedName", name);

            RequestDispatcher dispatcher = request.getRequestDispatcher("user_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Search failed", e);
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }
}