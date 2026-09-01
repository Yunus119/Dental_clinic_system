<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Find Patient</title>
    <script>
        // validates Sri Lankan phone format: 0771234567 or +94771234567
        function validateContact(input) {
            var pattern = /^(0\d{9}|\+94\d{9})$/;
            if (!pattern.test(input.value)) {
                input.setCustomValidity("Enter a valid number: 0771234567 or +94771234567");
            } else {
                input.setCustomValidity("");
            }
        }
    </script>
</head>
<body>

    <h2>Step 1: Find Patient</h2>

    <form action="makeAppointment" method="post">
        <input type="hidden" name="action" value="searchPatient">
        <label>Patient name:</label>
        <input type="text" name="patientName" required>
        <button type="submit">Search</button>
    </form>

    <br>

    <%
        List<Patient> patientResults = (List<Patient>) request.getAttribute("patientResults");
        String searchedName = (String) request.getAttribute("searchedName");
    %>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (patientResults != null) { %>

        <% if (patientResults.isEmpty()) { %>
            <p>No matching patient found for "<%= searchedName %>".</p>
        <% } else { %>
            <h3>Matching Patients</h3>
            <table border="1" cellpadding="5">
                <tr>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Address</th>
                    <th></th>
                </tr>
                <% for (Patient p : patientResults) { %>
                    <tr>
                        <td><%= p.getFirstName() %> <%= p.getLastName() %></td>
                        <td><%= p.getContactNumber() %></td>
                        <td><%= p.getAddress() %></td>
                        <td>
                            <form action="makeAppointment" method="post">
                                <input type="hidden" name="action" value="selectPatient">
                                <input type="hidden" name="patientId" value="<%= p.getPatientId() %>">
                                <button type="submit">Select</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- always available, whether or not matches were found -->
        <h3>None of these? Create New Patient</h3>
        <form action="makeAppointment" method="post">
            <input type="hidden" name="action" value="createPatient">

            <label>First Name:</label>
            <input type="text" name="firstName" required><br><br>

            <label>Last Name:</label>
            <input type="text" name="lastName" required><br><br>

            <label>Contact Number:</label>
            <input type="text" name="contactNumber" placeholder="0771234567 or +94771234567"
                   oninput="validateContact(this)" required><br><br>

            <label>Address:</label>
            <input type="text" name="address"><br><br>

            <button type="submit">Create and Select</button>
        </form>

    <% } %>

</body>
</html>