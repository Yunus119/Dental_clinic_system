package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.User;
import com.dentalclinic.service.UserService;

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));

            // blocks deleting your own account inside the service
            userService.deleteUser(userId, currentUser.getUserId());

            response.sendRedirect("userList");

        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            response.sendRedirect("userList?error=" + e.getMessage());

        } catch (Exception e) {
            throw new ServletException("Failed to delete user", e);
        }
    }
}