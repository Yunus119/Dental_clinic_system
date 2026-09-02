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

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();

    // shows the reset form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("userId"));
        request.setAttribute("userId", userId);

        RequestDispatcher dispatcher = request.getRequestDispatcher("reset_password.jsp");
        dispatcher.forward(request, response);
    }

    // handles the actual reset
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String newPassword = request.getParameter("newPassword");

            userService.resetPassword(userId, newPassword);

            request.setAttribute("message", "Password reset successfully");
            request.setAttribute("userId", userId);

            RequestDispatcher dispatcher = request.getRequestDispatcher("reset_password.jsp");
            dispatcher.forward(request, response);

        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("userId", request.getParameter("userId"));

            RequestDispatcher dispatcher = request.getRequestDispatcher("reset_password.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to reset password", e);
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