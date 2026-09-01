<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>View Appointments</title>
</head>
<body>

    <h2>View Appointments</h2>

    <%
        User currentUser = (User) session.getAttribute("currentUser");
        List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
    %>

    <% if (currentUser.getRole().equals("DOCTOR")) { %>

        <!-- doctor just picks a date range, no doctor selection needed -->
        <form action="appointmentList" method="post">
            <input type="hidden" name="action" value="viewAppointments">
            <label>From:</label>
            <input type="date" name="startDate" required>
            <label>To:</label>
            <input type="date" name="endDate" required>
            <button type="submit">View</button>
        </form>

    <% } else { %>

        <!-- admin/receptionist search for a doctor first -->
        <form action="appointmentList" method="post">
            <input type="hidden" name="action" value="searchDoctorForList">
            <label>Doctor name:</label>
            <input type="text" name="doctorName" required>
            <button type="submit">Search Doctor</button>
        </form>

        <br>

        <% if (doctorResults != null) { %>
            <% if (doctorResults.isEmpty()) { %>
                <p>No matching doctor found.</p>
            <% } else { %>
                <% for (User d : doctorResults) { %>
                    <form action="appointmentList" method="post" style="margin-bottom: 10px;">
                        <input type="hidden" name="action" value="viewAppointments">
                        <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                        <strong>Dr. <%= d.getFirstName() %> <%= d.getLastName() %></strong>
                        <label>From:</label>
                        <input type="date" name="startDate" required>
                        <label>To:</label>
                        <input type="date" name="endDate" required>
                        <button type="submit">View</button>
                    </form>
                <% } %>
            <% } %>
        <% } %>

    <% } %>

</body>
</html>