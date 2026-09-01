<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create User</title>
    <script>
        // builds a suggested username from first + last name
        // e.g. Joe Doe -> jdoe
        function generateUsername() {
            var firstName = document.getElementById("firstName").value.trim();
            var lastName = document.getElementById("lastName").value.trim();

            if (firstName.length > 0 && lastName.length > 0) {
                var suggested = (firstName.charAt(0) + lastName).toLowerCase();
                // strip out spaces or weird characters, just in case
                suggested = suggested.replace(/[^a-z0-9]/g, "");
                document.getElementById("username").value = suggested;
            }
        }
    </script>
</head>
<body>

    <h2>Create New User</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="createUser" method="post">

        <label>Role:</label>
        <select name="role">
            <option value="ADMIN">Admin</option>
            <option value="RECEPTIONIST">Receptionist</option>
            <option value="DOCTOR">Doctor</option>
        </select><br><br>

        <label>First Name:</label>
        <input type="text" id="firstName" name="firstName" onkeyup="generateUsername()" required><br><br>

        <label>Last Name:</label>
        <input type="text" id="lastName" name="lastName" onkeyup="generateUsername()" required><br><br>

        <label>Username:</label>
        <input type="text" id="username" name="username" required>
        <small>(auto-suggested, feel free to edit)</small><br><br>

        <label>Email:</label>
        <input type="email" name="email" required><br><br>

        <label>Password:</label>
        <input type="password" name="password" required><br><br>

        <button type="submit">Create User</button>
    </form>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>