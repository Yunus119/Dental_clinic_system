<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<%
    // this fragment expects "currentUser" to already be in the session
    // include it right after <body> on every logged-in page
    User currentUser = (User) session.getAttribute("currentUser");
%>

<div class="app-topbar">
    <div class="brand"><i class="fa fa-h-square"></i>Dental Clinic System</div>
    <div>Logged in as <strong><%= currentUser.getFirstName() %> <%= currentUser.getLastName() %></strong> (<%= currentUser.getRole() %>)</div>
</div>

<nav class="app-nav">
    <ul class="nav-links">
        <li><a href="dashboard.jsp">Dashboard</a></li>

        <% if (currentUser.getRole().equals("ADMIN")) { %>
            <li><a href="createUser">Create User</a></li>
            <li><a href="userList">User List</a></li>
            <li><a href="treatmentList">Treatment Types</a></li>
        <% } %>

        <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
            <li><a href="doctorList">Doctors</a></li>
            <li><a href="makeAppointment">Make Appointment</a></li>
            <li><a href="bill">Calculate Bill</a></li>
            <li><a href="report">Reports</a></li>
        <% } %>

        <li><a href="appointmentList">Appointments</a></li>
        <li><a href="patientList">Patients</a></li>
        <li><a href="help">Help</a></li>
    </ul>

    <ul class="nav-links nav-logout">
        <li><a href="logout">Logout</a></li>
    </ul>
</nav>
