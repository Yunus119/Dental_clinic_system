<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
</head>
<body>

    <h2>Reset Password</h2>

    <% if (request.getAttribute("message") != null) { %>
        <p style="color:green;"><%= request.getAttribute("message") %></p>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="resetPassword" method="post">
        <input type="hidden" name="userId" value="<%= request.getAttribute("userId") %>">

        <label>New Password:</label>
        <input type="password" name="newPassword" required>

        <button type="submit">Reset Password</button>
    </form>

    <br>
    <a href="userList">Back to User List</a>

</body>
</html>