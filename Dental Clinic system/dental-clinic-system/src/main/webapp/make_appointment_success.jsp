<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Appointment" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointment Booked</title>
</head>
<body>

    <h2>Appointment booked successfully!</h2>

    <%
        Appointment booked = (Appointment) request.getAttribute("bookedAppointment");
    %>

    <p>Appointment number: <%= booked.getAppointmentNumber() %></p>
    <p>Date/time: <%= booked.getAppointmentDateTime() %></p>
    <p>Status: <%= booked.getStatus() %></p>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>