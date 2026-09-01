<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Choose Doctor</title>
</head>
<body>

    <h2>Step 1: Choose a Doctor</h2>

    <form action="makeAppointment" method="post">
        <input type="hidden" name="action" value="searchDoctor">
        <label>Search by name:</label>
        <input type="text" name="doctorName" placeholder="Leave blank to browse all">
        <button type="submit">Search</button>
    </form>

    <br>

    <%
        List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
    %>

    <% if (doctorResults != null && !doctorResults.isEmpty()) { %>
        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; max-width: 700px;">
            <% for (User d : doctorResults) { %>
                <form action="makeAppointment" method="post">
                    <input type="hidden" name="action" value="selectDoctor">
                    <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                    <button type="submit" style="padding: 15px; width: 100%; text-align: left;">
                        <strong>Dr. <%= d.getFirstName() %> <%= d.getLastName() %></strong><br>
                        <%= d.getEmail() %>
                    </button>
                </form>
            <% } %>
        </div>
    <% } else if (doctorResults != null) { %>
        <p>No matching doctor found.</p>
    <% } %>

    <!-- pagination controls - only shown when browsing (not after a search) -->
    <%
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
    %>
    <% if (currentPage != null) { %>
        <br>
        <% if (currentPage > 1) { %>
            <a href="makeAppointment?page=<%= currentPage - 1 %>">&laquo; Previous</a>
        <% } %>

        <span> Page <%= currentPage %> of <%= totalPages %> </span>

        <% if (currentPage < totalPages) { %>
            <a href="makeAppointment?page=<%= currentPage + 1 %>">Next &raquo;</a>
        <% } %>
    <% } %>

</body>
</html>