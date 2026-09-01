<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.model.Appointment" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointments</title>
</head>
<body>

    <h2>Appointments: <%= request.getAttribute("startDate") %> to <%= request.getAttribute("endDate") %></h2>

    <%
        List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
        DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    %>

    <% if (appointments.isEmpty()) { %>
        <p>No appointments in this range.</p>
    <% } else { %>
        <table border="1" cellpadding="5">
            <tr>
                <th>Appointment #</th>
                <th>Date/Time</th>
                <th>Status</th>
            </tr>
            <% for (Appointment a : appointments) { %>
                <tr>
                    <td><%= a.getAppointmentNumber() %></td>
                    <td><%= a.getAppointmentDateTime().format(niceFormat) %></td>
                    <td><%= a.getStatus() %></td>
                </tr>
            <% } %>
        </table>
    <% } %>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>