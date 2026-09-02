package com.dentalclinic.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/help")
public class HelpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // just shows the static help page - available to everyone logged in
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher("help.jsp");
        dispatcher.forward(request, response);
    }
}