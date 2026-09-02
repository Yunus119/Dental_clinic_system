<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>
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
        #confirmDialog { text-align: center; }
        #confirmDialog i { font-size: 34px; color: var(--color-accent); margin-bottom: 10px; }
        #confirmDialog p { color: var(--color-darker); font-size: 14px; margin-bottom: 25px; }
        #confirmDialog .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    </style>
    <script>
        var formPendingSubmit = null;

        // validates the form first, then shows the confirm modal
        function openUpdateConfirm() {
            var form = document.getElementById("updateUserForm");

            if (!form.reportValidity()) {
                return;
            }

            formPendingSubmit = form;
            document.getElementById("confirmMessage").textContent = "Save these changes to this user?";
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

        <h2 class="page-title">Update User</h2>

        <%
            User editUser = (User) request.getAttribute("user");
        %>

        <div class="form-card">
            <form action="updateUser" method="post" id="updateUserForm">
                <input type="hidden" name="userId" value="<%= editUser.getUserId() %>">

                <div class="form-group">
                    <label>Username <span class="text-muted">(cannot be changed)</span></label>
                    <input type="text" value="<%= editUser.getUsername() %>" disabled>
                </div>

                <div class="form-group">
                    <label>First Name</label>
                    <input type="text" name="firstName" value="<%= editUser.getFirstName() %>" required>
                </div>

                <div class="form-group">
                    <label>Last Name</label>
                    <input type="text" name="lastName" value="<%= editUser.getLastName() %>" required>
                </div>

                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value="<%= editUser.getEmail() %>" required>
                </div>

                <div class="form-group">
                    <label>Role</label>
                    <select name="role">
                        <option value="ADMIN" <%= editUser.getRole().equals("ADMIN") ? "selected" : "" %>>Admin</option>
                        <option value="RECEPTIONIST" <%= editUser.getRole().equals("RECEPTIONIST") ? "selected" : "" %>>Receptionist</option>
                        <option value="DOCTOR" <%= editUser.getRole().equals("DOCTOR") ? "selected" : "" %>>Doctor</option>
                    </select>
                </div>

                <button type="button" class="btn btn-primary" onclick="openUpdateConfirm();">Save Changes</button>
            </form>
        </div>

        <br>
        <a href="userList">Back to User List</a>

        <!-- shared custom confirm modal -->
        <dialog id="confirmDialog">
            <i class="fa fa-check-circle"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-primary" onclick="confirmYes();">Yes, Save</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Cancel</button>
            </div>
        </dialog>

    </div>

</body>
</html>
