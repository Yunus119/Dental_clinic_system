<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>User List</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        #confirmDialog { text-align: center; }
        #confirmDialog i { font-size: 34px; color: var(--color-danger); margin-bottom: 10px; }
        #confirmDialog p { color: var(--color-darker); font-size: 14px; margin-bottom: 25px; }
        #confirmDialog .confirm-actions { display: flex; gap: 10px; justify-content: center; }
        .filter-bar { display: flex; gap: 12px; flex-wrap: wrap; align-items: flex-end; margin-bottom: 25px; }
        .filter-bar .form-group { margin-bottom: 0; }
        .filter-bar input, .filter-bar select { width: 200px; }
    </style>
    <script>
        var formPendingSubmit = null;
        function askConfirm(button, message) {
            formPendingSubmit = button.closest("form");
            document.getElementById("confirmMessage").textContent = message;
            document.getElementById("confirmDialog").showModal();
        }
        function confirmYes() {
            document.getElementById("confirmDialog").close();
            if (formPendingSubmit) { formPendingSubmit.submit(); }
        }
        function confirmNo() {
            document.getElementById("confirmDialog").close();
            formPendingSubmit = null;
        }
    </script>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">All Users</h2>

        <%
            String nameFilter = (String) request.getAttribute("nameFilter");
            String roleFilter = (String) request.getAttribute("roleFilter");
        %>

        <form action="userList" method="get" class="filter-bar">
            <div class="form-group">
                <label>Search by name</label>
                <input type="text" name="searchName" value="<%= nameFilter != null ? nameFilter : "" %>">
            </div>

            <div class="form-group">
                <label>Role</label>
                <select name="role">
                    <option value="">All Roles</option>
                    <option value="ADMIN" <%= "ADMIN".equals(roleFilter) ? "selected" : "" %>>Admin</option>
                    <option value="RECEPTIONIST" <%= "RECEPTIONIST".equals(roleFilter) ? "selected" : "" %>>Receptionist</option>
                    <option value="DOCTOR" <%= "DOCTOR".equals(roleFilter) ? "selected" : "" %>>Doctor</option>
                </select>
            </div>

            <button type="submit" class="btn btn-dark">Filter</button>
            <a href="userList" class="btn btn-outline">Clear</a>
        </form>

        <%
            List<User> users = (List<User>) request.getAttribute("users");
        %>

        <% if (users.isEmpty()) { %>
            <p>No users found.</p>
        <% } else { %>
            <table class="app-table">
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
                        <td><span class="badge badge-scheduled"><%= u.getRole() %></span></td>
                        <td>
                            <a href="updateUser?userId=<%= u.getUserId() %>" class="btn btn-outline btn-sm">Update</a>
                            <a href="resetPassword?userId=<%= u.getUserId() %>" class="btn btn-outline btn-sm">Reset Password</a>
                            <form action="deleteUser" method="post" style="display:inline;">
                                <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                <button type="button" class="btn btn-danger btn-sm"
                                        onclick="askConfirm(this, 'Delete <%= u.getFirstName() %> <%= u.getLastName() %>? This cannot be undone.');">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- pagination - always shown now, filters carried along in the links -->
        <%
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");

            StringBuilder filterQuery = new StringBuilder();
            if (nameFilter != null && !nameFilter.isBlank()) filterQuery.append("&searchName=").append(nameFilter);
            if (roleFilter != null && !roleFilter.isBlank()) filterQuery.append("&role=").append(roleFilter);
            String filters = filterQuery.toString();
        %>
        <div class="pagination">
            <% if (currentPage > 1) { %>
                <a href="userList?page=<%= currentPage - 1 %><%= filters %>">&laquo; Previous</a>
            <% } %>
            <strong>Page <%= currentPage %> of <%= totalPages %></strong>
            <% if (currentPage < totalPages) { %>
                <a href="userList?page=<%= currentPage + 1 %><%= filters %>">Next &raquo;</a>
            <% } %>
        </div>

        <!-- shared custom confirm modal for delete -->
        <dialog id="confirmDialog">
            <i class="fa fa-exclamation-triangle"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-danger" onclick="confirmYes();">Yes, Delete</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Cancel</button>
            </div>
        </dialog>

    </div>

</body>
</html>
