<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Patient List</title>
</head>
<body>

    <h2>All Patients</h2>

    <form action="patientList" method="post">
        <label>Search by name:</label>
        <input type="text" name="searchName" required>
        <button type="submit">Search</button>
    </form>

    <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

    <% if (isSearchResult != null && isSearchResult) { %>
        <p>Showing search results for "<%= request.getAttribute("searchedName") %>" -
           <a href="patientList">Clear search</a></p>
    <% } %>

    <%
        List<Patient> patients = (List<Patient>) request.getAttribute("patients");
        User currentUser = (User) session.getAttribute("currentUser");
        boolean canEdit = !currentUser.getRole().equals("DOCTOR");
    %>

    <% if (patients.isEmpty()) { %>
        <p>No patients found.</p>
    <% } else { %>
        <table border="1" cellpadding="5">
            <tr>
                <th>Name</th>
                <th>Contact</th>
                <th>Address</th>
                <% if (canEdit) { %><th>Actions</th><% } %>
            </tr>
            <% for (Patient p : patients) { %>
                <tr>
                    <td><%= p.getFirstName() %> <%= p.getLastName() %></td>
                    <td><%= p.getContactNumber() %></td>
                    <td><%= p.getAddress() %></td>
                    <% if (canEdit) { %>
                        <td>
                            <a href="updatePatient?patientId=<%= p.getPatientId() %>">Update</a>
                        </td>
                    <% } %>
                </tr>
            <% } %>
        </table>
    <% } %>

    <!-- pagination - only shown when not searching -->
    <% if (isSearchResult == null) {
        int currentPage = (Integer) request.getAttribute("currentPage");
        int totalPages = (Integer) request.getAttribute("totalPages");
    %>
        <br>
        <% if (currentPage > 1) { %>
            <a href="patientList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
        <% } %>

        <span> Page <%= currentPage %> of <%= totalPages %> </span>

        <% if (currentPage < totalPages) { %>
            <a href="patientList?page=<%= currentPage + 1 %>">Next &raquo;</a>
        <% } %>
    <% } %>

    <br><br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>