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
import com.dentalclinic.service.UserService;

@WebServlet("/updateUser")
public class UpdateUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();

    // shows the edit form, pre-filled with the user's current details
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = userService.getUserById(userId);

            request.setAttribute("user", user);

            RequestDispatcher dispatcher = request.getRequestDispatcher("update_user.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load user", e);
        }
    }

    // saves the edited details
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = userService.getUserById(userId);

            // apply the edited fields onto the existing user object
            user.setFirstName(request.getParameter("firstName"));
            user.setLastName(request.getParameter("lastName"));
            user.setEmail(request.getParameter("email"));
            user.setRole(request.getParameter("role"));

            userService.updateUser(user);

            response.sendRedirect("userList");

        } catch (Exception e) {
            throw new ServletException("Failed to update user", e);
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