<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Choose Patient</title>
</head>
<body>

    <h2>Step 3: Choose Patient</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <!-- search box -->
    <form action="makeAppointment" method="post">
        <input type="hidden" name="action" value="searchPatient">
        <label>Patient name:</label>
        <input type="text" name="patientName" required>
        <button type="submit">Search</button>
    </form>

    <!-- opens the popup dialog to create a new patient -->
    <button type="button" onclick="document.getElementById('newPatientDialog').showModal();">
        + Create New Patient
    </button>

    <br><br>

    <%
        List<Patient> patientResults = (List<Patient>) request.getAttribute("patientResults");
        String searchedName = (String) request.getAttribute("searchedName");
    %>

    <% if (patientResults != null) { %>

        <% if (patientResults.isEmpty()) { %>
            <p>No matching patient found for "<%= searchedName %>". Use "Create New Patient" above.</p>
        <% } else { %>
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
                                <input type="hidden" name="action" value="selectPatientAndBook">
                                <input type="hidden" name="patientId" value="<%= p.getPatientId() %>">
                                <button type="submit"
                                        onclick="return confirm('Book this appointment for <%= p.getFirstName() %> <%= p.getLastName() %>?');">
                                    Select and Book
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>
    <% } %>

    <!-- the popup dialog itself - a native HTML element, no JS framework needed -->
    <dialog id="newPatientDialog" style="padding: 20px;">
        <h3>Create New Patient</h3>
        <form action="makeAppointment" method="post">
            <input type="hidden" name="action" value="createPatientAndBook">

            <label>First Name:</label>
            <input type="text" name="firstName" required><br><br>

            <label>Last Name:</label>
            <input type="text" name="lastName" required><br><br>

            <label>Contact Number:</label>
            <input type="text" name="contactNumber" placeholder="0771234567 or +94771234567" required><br><br>

            <label>Address:</label>
            <input type="text" name="address"><br><br>

            <button type="submit"
                    onclick="return confirm('Create this patient and book the appointment?');">
                Create and Book
            </button>
            <button type="button" onclick="document.getElementById('newPatientDialog').close();">Cancel</button>
        </form>
    </dialog>

</body>
</html>