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

    // shows the filtered, paginated user list - everything lives in the URL
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            String nameFilter = request.getParameter("searchName");
            String roleFilter = request.getParameter("role");
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;

            List<User> users = userService.searchUsersFiltered(nameFilter, roleFilter, page, PAGE_SIZE);
            int totalUsers = userService.countUsersFiltered(nameFilter, roleFilter);
            int totalPages = Math.max((int) Math.ceil((double) totalUsers / PAGE_SIZE), 1);

            request.setAttribute("users", users);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("nameFilter", nameFilter);
            request.setAttribute("roleFilter", roleFilter);

            RequestDispatcher dispatcher = request.getRequestDispatcher("user_list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load users", e);
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