<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>

    <%
        User currentUser = (User) session.getAttribute("currentUser");
    %>

    <h2>Welcome, <%= currentUser.getFirstName() %>!</h2>

    <% if (currentUser.getRole().equals("ADMIN")) { %>
        <p><a href="createUser">Create New User</a></p>
    <% } %>

    <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
        <p><a href="makeAppointment">Make Appointment</a></p>
        <p><a href="bill">Calculate Bill</a></p>
    <% } %>

    <% if (currentUser.getRole().equals("DOCTOR")) { %>
        <p><a href="appointmentList">My Appointments</a></p>
    <% } %>

    <a href="logout">Logout</a>

</body>
</html>