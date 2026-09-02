<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Help</title>
</head>
<body>

    <h2>Help & Instructions</h2>

    <%
        User currentUser = (User) session.getAttribute("currentUser");
    %>

    <h3>Logging In / Out</h3>
    <p>Enter your username and password on the login page and click Login. Click Logout in the top menu at any time to end your session.</p>

    <% if (currentUser.getRole().equals("ADMIN")) { %>
        <h3>For Admins</h3>
        <ul>
            <li><strong>Create New User:</strong> Fill in the role, name, username, email and password, then submit.</li>
            <li><strong>User List:</strong> View, search, update, delete, or reset the password of any user.</li>
            <li><strong>Treatment Types:</strong> Create or update the treatments the clinic offers and their prices.</li>
        </ul>
    <% } %>

    <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
        <h3>For Receptionists</h3>
        <ul>
            <li><strong>Make Appointment:</strong> Choose a doctor, pick a date to see their available time slots, then find or create the patient to complete the booking.</li>
            <li><strong>Calculate Bill:</strong> Search for the doctor and appointment, then calculate and print the bill.</li>
            <li><strong>Appointment List:</strong> Search by doctor, patient, date, or appointment number. From here you can also update (reschedule) or cancel an appointment.</li>
            <li><strong>Patient List:</strong> View and update existing patient details.</li>
            <li><strong>Reports:</strong> Pick a date range to see appointment counts, revenue, and popular treatments, or download as CSV.</li>
        </ul>
    <% } %>

    <% if (currentUser.getRole().equals("DOCTOR")) { %>
        <h3>For Doctors</h3>
        <ul>
            <li><strong>View Appointments:</strong> See your upcoming appointments, or filter by date and patient name.</li>
            <li><strong>Patient List:</strong> View patient details (read-only).</li>
        </ul>
    <% } %>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>