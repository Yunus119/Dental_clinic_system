package com.dentalclinic.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dentalclinic.model.User;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.SlotInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

// REST endpoint returning a doctor's schedule as JSON
// example: /api/schedule?doctorId=3&date=2026-09-15
@WebServlet("/api/schedule")
public class ScheduleApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();

    // tell Gson exactly how to turn a LocalTime into JSON
    // needed because Java 17 blocks reflection into java.time internals
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .create();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }

        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            LocalDate date = LocalDate.parse(request.getParameter("date"));

            List<SlotInfo> schedule = appointmentService.getDaySchedule(doctorId, date);

            String json = gson.toJson(schedule);
            out.write(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}