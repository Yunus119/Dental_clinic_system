<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create User</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .form-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 35px;
            max-width: 500px;
        }
        .password-wrapper {
            position: relative;
        }
        .password-wrapper input {
            padding-right: 40px;
        }
        .toggle-password {
            position: absolute;
            right: 14px;
            top: 12px;
            cursor: pointer;
            color: var(--color-text);
        }
        .match-hint {
            font-size: 12px;
            margin-top: 6px;
            font-weight: 500;
        }
        .match-hint.match {
            color: #5a6b0f;
        }
        .match-hint.no-match {
            color: #a3312a;
        }
    </style>
    <script>
        // builds a suggested username from first + last name
        function generateUsername() {
            var firstName = document.getElementById("firstName").value.trim();
            var lastName = document.getElementById("lastName").value.trim();

            if (firstName.length > 0 && lastName.length > 0) {
                var suggested = (firstName.charAt(0) + lastName).toLowerCase();
                suggested = suggested.replace(/[^a-z0-9]/g, "");
                document.getElementById("username").value = suggested;
            }
        }

        // shows a live match / no-match message as the person types
        function checkPasswordsLive() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirmPassword").value;
            var hint = document.getElementById("matchHint");

            if (confirmPassword.length === 0) {
                hint.textContent = "";
                hint.className = "match-hint";
                return;
            }

            if (password === confirmPassword) {
                hint.textContent = "Passwords match";
                hint.className = "match-hint match";
            } else {
                hint.textContent = "Passwords do not match";
                hint.className = "match-hint no-match";
            }
        }

        // final check before the form actually submits
        function checkPasswordsMatch() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirmPassword").value;

            if (password !== confirmPassword) {
                checkPasswordsLive();
                return false;
            }
            return true;
        }

        // toggles a password field between hidden and visible text
        function togglePassword(fieldId, iconEl) {
            var field = document.getElementById(fieldId);
            if (field.type === "password") {
                field.type = "text";
                iconEl.classList.remove("fa-eye");
                iconEl.classList.add("fa-eye-slash");
            } else {
                field.type = "password";
                iconEl.classList.remove("fa-eye-slash");
                iconEl.classList.add("fa-eye");
            }
        }
    </script>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Create New User</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <div class="form-card">
            <form action="createUser" method="post" onsubmit="return checkPasswordsMatch();">

                <div class="form-group">
                    <label>Role</label>
                    <select name="role">
                        <option value="ADMIN">Admin</option>
                        <option value="RECEPTIONIST">Receptionist</option>
                        <option value="DOCTOR">Doctor</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>First Name</label>
                    <input type="text" id="firstName" name="firstName" onkeyup="generateUsername()" required>
                </div>

                <div class="form-group">
                    <label>Last Name</label>
                    <input type="text" id="lastName" name="lastName" onkeyup="generateUsername()" required>
                </div>

                <div class="form-group">
                    <label>Username <span class="text-muted">(auto-suggested, feel free to edit)</span></label>
                    <input type="text" id="username" name="username" required>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" required>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="password" name="password" onkeyup="checkPasswordsLive()" required>
                        <i class="fa fa-eye toggle-password" onclick="togglePassword('password', this)"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label>Confirm Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="confirmPassword" name="confirmPassword" onkeyup="checkPasswordsLive()" required>
                        <i class="fa fa-eye toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div>
                    <div id="matchHint" class="match-hint"></div>
                </div>

                <button type="submit" class="btn btn-primary">Create User</button>
            </form>
        </div>

        <br>
        <a href="dashboard.jsp">Back to Dashboard</a>

    </div>

</body>
</html>
