<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>
</head>
<body>

    <h2>Update User</h2>

    <%
        User user = (User) request.getAttribute("user");
    %>

    <form action="updateUser" method="post">
        <input type="hidden" name="userId" value="<%= user.getUserId() %>">

        <label>Username (cannot be changed):</label>
        <input type="text" value="<%= user.getUsername() %>" disabled><br><br>

        <label>First Name:</label>
        <input type="text" name="firstName" value="<%= user.getFirstName() %>" required><br><br>

        <label>Last Name:</label>
        <input type="text" name="lastName" value="<%= user.getLastName() %>" required><br><br>

        <label>Email:</label>
        <input type="email" name="email" value="<%= user.getEmail() %>" required><br><br>

        <label>Role:</label>
        <select name="role">
            <option value="ADMIN" <%= user.getRole().equals("ADMIN") ? "selected" : "" %>>Admin</option>
            <option value="RECEPTIONIST" <%= user.getRole().equals("RECEPTIONIST") ? "selected" : "" %>>Receptionist</option>
            <option value="DOCTOR" <%= user.getRole().equals("DOCTOR") ? "selected" : "" %>>Doctor</option>
        </select><br><br>

        <button type="submit">Save Changes</button>
    </form>

    <br>
    <a href="userList">Back to User List</a>

</body>
</html>