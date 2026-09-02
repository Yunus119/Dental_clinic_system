<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Patient</title>
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

        // validates the form first (letters-only names, phone format), then shows the confirm modal
        function openUpdateConfirm() {
            var form = document.getElementById("updatePatientForm");

            if (!form.reportValidity()) {
                return;
            }

            formPendingSubmit = form;
            document.getElementById("confirmMessage").textContent = "Save these changes to this patient?";
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

        <h2 class="page-title">Update Patient</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <%
            Patient editPatient = (Patient) request.getAttribute("patient");
        %>

        <div class="form-card">
            <form action="updatePatient" method="post" id="updatePatientForm">
                <input type="hidden" name="patientId" value="<%= editPatient.getPatientId() %>">

                <div class="form-group">
                    <label>First Name</label>
                    <input type="text" name="firstName" value="<%= editPatient.getFirstName() %>"
                           pattern="[a-zA-Z\s'-]+" title="Letters only" required>
                </div>

                <div class="form-group">
                    <label>Last Name</label>
                    <input type="text" name="lastName" value="<%= editPatient.getLastName() %>"
                           pattern="[a-zA-Z\s'-]+" title="Letters only" required>
                </div>

                <div class="form-group">
                    <label>Contact Number</label>
                    <input type="text" name="contactNumber" value="<%= editPatient.getContactNumber() %>"
                           pattern="(0\d{9}|\+94\d{9})" title="Format: 0771234567 or +94771234567"
                           maxlength="13" placeholder="0771234567 or +94771234567" required>
                </div>

                <div class="form-group">
                    <label>Address</label>
                    <input type="text" name="address" value="<%= editPatient.getAddress() %>">
                </div>

                <button type="button" class="btn btn-primary" onclick="openUpdateConfirm();">Save Changes</button>
            </form>
        </div>

        <br>
        <a href="patientList">Back to Patient List</a>

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
