<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Appointment, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Calculate Bill - Find Appointment</title>
</head>
<body>

    <h2>Find Appointment to Bill</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <!-- step 1: search doctor -->
    <form action="bill" method="post">
        <input type="hidden" name="action" value="searchDoctorForBill">
        <label>Doctor name:</label>
        <input type="text" name="doctorName" required>
        <button type="submit">Search Doctor</button>
    </form>

    <br>

    <%
        List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
    %>

    <% if (doctorResults != null) { %>
        <h3>Select Doctor</h3>
        <% for (User d : doctorResults) { %>
            <form action="bill" method="post" style="display:inline-block; margin-right:10px;">
                <input type="hidden" name="action" value="searchAppointmentsForBill">
                <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                <label>Date:</label>
                <input type="date" name="appointmentDate" required>
                <button type="submit">Find <%= d.getFirstName() %> <%= d.getLastName() %>'s Appointments</button>
            </form>
            <br><br>
        <% } %>
    <% } %>

    <%
        List<Appointment> appointmentResults = (List<Appointment>) request.getAttribute("appointmentResults");
    %>

    <% if (appointmentResults != null) { %>
        <h3>Appointments</h3>
        <% if (appointmentResults.isEmpty()) { %>
            <p>No appointments found for that doctor on that date.</p>
        <% } else { %>
            <table border="1" cellpadding="5">
                <tr>
                    <th>Appointment #</th>
                    <th>Date/Time</th>
                    <th>Status</th>
                    <th></th>
                </tr>
                <% for (Appointment a : appointmentResults) { %>
                    <tr>
                        <td><%= a.getAppointmentNumber() %></td>
                        <td><%= a.getAppointmentDateTime() %></td>
                        <td><%= a.getStatus() %></td>
                        <td>
                            <form action="bill" method="post">
                                <input type="hidden" name="action" value="calculateBill">
                                <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>">
                                <button type="submit">Calculate Bill</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>
    <% } %>

</body>
</html>