<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Doctor List</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .doctor-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 14px;
            margin-top: 20px;
        }
        .doctor-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 20px;
        }
        .doctor-card i {
            font-size: 22px;
            color: var(--color-accent);
            margin-bottom: 8px;
            display: block;
        }
        .doctor-card strong {
            display: block;
            font-size: 15px;
            color: var(--color-darker);
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Doctor List</h2>

        <form action="doctorList" method="post" style="margin-bottom: 20px; display: flex; gap: 10px; align-items: flex-end; max-width: 500px;">
            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                <label>Search by name</label>
                <input type="text" name="searchName" placeholder="Leave blank to browse all" required>
            </div>
            <button type="submit" class="btn btn-dark">Search</button>
        </form>

        <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

        <% if (isSearchResult != null && isSearchResult) { %>
            <div class="alert alert-success">
                Showing results for "<%= request.getAttribute("searchedName") %>" &nbsp;
                <a href="doctorList" style="text-decoration:underline;">Clear search</a>
            </div>
        <% } %>

        <%
            List<User> doctors = (List<User>) request.getAttribute("doctors");
        %>

        <% if (doctors.isEmpty()) { %>
            <p>No doctors found.</p>
        <% } else { %>
            <div class="doctor-grid">
                <% for (User d : doctors) { %>
                    <div class="doctor-card">
                        <i class="fa fa-user-md"></i>
                        <strong>Dr. <%= d.getFirstName() %> <%= d.getLastName() %></strong>
                        <span class="text-muted"><%= d.getEmail() %></span>
                    </div>
                <% } %>
            </div>
        <% } %>

        <!-- pagination - only shown for the normal (non-search) view -->
        <% if (isSearchResult == null) {
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");
        %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="doctorList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
                <% } %>
                <strong>Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <a href="doctorList?page=<%= currentPage + 1 %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>

    </div>

</body>
</html>
