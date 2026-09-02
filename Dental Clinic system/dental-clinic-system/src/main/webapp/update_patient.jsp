<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Patient</title>
</head>
<body>

    <h2>Update Patient</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <%
        Patient patient = (Patient) request.getAttribute("patient");
    %>

    <form action="updatePatient" method="post">
        <input type="hidden" name="patientId" value="<%= patient.getPatientId() %>">

        <label>First Name:</label>
        <input type="text" name="firstName" value="<%= patient.getFirstName() %>"
               pattern="[a-zA-Z\s'-]+" title="Letters only" required><br><br>

        <label>Last Name:</label>
        <input type="text" name="lastName" value="<%= patient.getLastName() %>"
               pattern="[a-zA-Z\s'-]+" title="Letters only" required><br><br>

        <label>Contact Number:</label>
        <input type="text" name="contactNumber" value="<%= patient.getContactNumber() %>"
               pattern="(0\d{9}|\+94\d{9})" title="Format: 0771234567 or +94771234567"
               maxlength="13" placeholder="0771234567 or +94771234567" required><br><br>

        <label>Address:</label>
        <input type="text" name="address" value="<%= patient.getAddress() %>"><br><br>

        <button type="submit">Save Changes</button>
    </form>

    <br>
    <a href="patientList">Back to Patient List</a>

</body>
</html>