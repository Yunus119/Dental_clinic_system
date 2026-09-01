<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>User List</title>
</head>
<body>

    <h2>All Users</h2>

    <!-- search box -->
    <form action="userList" method="post">
        <label>Search by name:</label>
        <input type="text" name="searchName" required>
        <button type="submit">Search</button>
    </form>

    <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

    <% if (isSearchResult != null && isSearchResult) { %>
        <p>Showing search results for "<%= request.getAttribute("searchedName") %>" -
           <a href="userList">Clear search</a></p>
    <% } %>

    <%
        List<User> users = (List<User>) request.getAttribute("users");
    %>

    <% if (users.isEmpty()) { %>
        <p>No users found.</p>
    <% } else { %>
        <table border="1" cellpadding="5">
            <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
            </tr>
            <% for (User u : users) { %>
                <tr>
                    <td><%= u.getFirstName() %> <%= u.getLastName() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><%= u.getEmail() %></td>
                    <td><%= u.getRole() %></td>
                    <td>
                        <a href="updateUser?userId=<%= u.getUserId() %>">Update</a> |
                        <a href="resetPassword?userId=<%= u.getUserId() %>">Reset Password</a> |
                        <a href="deleteUser?userId=<%= u.getUserId() %>"
                           onclick="return confirm('Delete <%= u.getFirstName() %> <%= u.getLastName() %>? This cannot be undone.');">
                            Delete
                        </a>
                    </td>
                </tr>
            <% } %>
        </table>
    <% } %>

    <!-- pagination controls - only shown for the normal (non-search) view -->
    <% if (isSearchResult == null) {
        int currentPage = (Integer) request.getAttribute("currentPage");
        int totalPages = (Integer) request.getAttribute("totalPages");
    %>
        <br>
        <% if (currentPage > 1) { %>
            <a href="userList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
        <% } %>

        <span> Page <%= currentPage %> of <%= totalPages %> </span>

        <% if (currentPage < totalPages) { %>
            <a href="userList?page=<%= currentPage + 1 %>">Next &raquo;</a>
        <% } %>
    <% } %>

    <br><br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>