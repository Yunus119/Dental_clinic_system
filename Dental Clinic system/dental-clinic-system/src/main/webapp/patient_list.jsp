<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Patient List</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">All Patients</h2>

        <form action="patientList" method="post" style="margin-bottom: 20px; display: flex; gap: 10px; align-items: flex-end; max-width: 500px;">
            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                <label>Search by name</label>
                <input type="text" name="searchName" required>
            </div>
            <button type="submit" class="btn btn-dark">Search</button>
        </form>

        <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

        <% if (isSearchResult != null && isSearchResult) { %>
            <div class="alert alert-success">
                Showing results for "<%= request.getAttribute("searchedName") %>" &nbsp;
                <a href="patientList" style="text-decoration:underline;">Clear search</a>
            </div>
        <% } %>

        <%
            // currentUser already provided by common_nav.jsp - do not redeclare
            List<Patient> patients = (List<Patient>) request.getAttribute("patients");
            boolean canEdit = !currentUser.getRole().equals("DOCTOR");
        %>

        <% if (patients.isEmpty()) { %>
            <p>No patients found.</p>
        <% } else { %>
            <table class="app-table">
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
                                <a href="updatePatient?patientId=<%= p.getPatientId() %>" class="btn btn-outline btn-sm">Update</a>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- pagination - only shown for the normal (non-search) view -->
        <% if (isSearchResult == null) {
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");
        %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="patientList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
                <% } %>
                <strong>Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <a href="patientList?page=<%= currentPage + 1 %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>

    </div>

</body>
</html>
