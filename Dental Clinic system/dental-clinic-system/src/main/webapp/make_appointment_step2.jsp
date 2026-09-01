<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Find Doctor</title>
</head>
<body>

    <h2>Step 2: Find Doctor</h2>

    <% if (request.getAttribute("message") != null) { %>
        <p style="color:green;"><%= request.getAttribute("message") %></p>
    <% } %>

    <form action="makeAppointment" method="post">
        <input type="hidden" name="action" value="searchDoctor">
        <label>Doctor name:</label>
        <input type="text" name="doctorName" required>
        <button type="submit">Search</button>
    </form>

    <br>

    <%
        List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
        String searchedDoctorName = (String) request.getAttribute("searchedDoctorName");
    %>

    <% if (doctorResults != null) { %>

        <% if (doctorResults.isEmpty()) { %>
            <p>No matching doctor found for "<%= searchedDoctorName %>".</p>
        <% } else { %>
            <h3>Matching Doctors</h3>
            <table border="1" cellpadding="5">
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th></th>
                </tr>
                <% for (User d : doctorResults) { %>
                    <tr>
                        <td><%= d.getFirstName() %> <%= d.getLastName() %></td>
                        <td><%= d.getEmail() %></td>
                        <td>
                            <form action="makeAppointment" method="post">
                                <input type="hidden" name="action" value="selectDoctor">
                                <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                                <button type="submit">Select</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>
    <% } %>

</body>
</html>