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
        <p><a href="userList">User List</a></p>
    	<p><a href="treatmentList">Treatment Types</a></p>        
    <% } %>

    <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
        <p><a href="makeAppointment">Make Appointment</a></p>
        <p><a href="bill">Calculate Bill</a></p>
        <p><a href="report">Reports</a></p>
    <% } %>
    
    <% if (currentUser.getRole().equals("ADMIN")) { %>
    	<p><a href="createUser">Create New User</a></p>
    	<p><a href="userList">User List</a></p>
	<% } %>

	<p><a href="patientList">Patient List</a></p>

    <p><a href="appointmentList">View Appointments</a></p>
    
    <p><a href="help">Help</a></p>

    <a href="logout">Logout</a>

</body>
</html>