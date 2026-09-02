<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .form-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 35px;
            max-width: 420px;
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
        // shows a live match / no-match message as the person types
        function checkPasswordsLive() {
            var password = document.getElementById("newPassword").value;
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
            var password = document.getElementById("newPassword").value;
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

        <h2 class="page-title">Reset Password</h2>

        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("message") %></div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <div class="form-card">
            <form action="resetPassword" method="post" onsubmit="return checkPasswordsMatch();">
                <input type="hidden" name="userId" value="<%= request.getAttribute("userId") %>">

                <div class="form-group">
                    <label>New Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="newPassword" name="newPassword" onkeyup="checkPasswordsLive()" required>
                        <i class="fa fa-eye toggle-password" onclick="togglePassword('newPassword', this)"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label>Confirm New Password</label>
                    <div class="password-wrapper">
                        <input type="password" id="confirmPassword" name="confirmPassword" onkeyup="checkPasswordsLive()" required>
                        <i class="fa fa-eye toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div>
                    <div id="matchHint" class="match-hint"></div>
                </div>

                <button type="submit" class="btn btn-primary">Reset Password</button>
            </form>
        </div>

        <br>
        <a href="userList">Back to User List</a>

    </div>

</body>
</html>
