<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Treatment Type</title>
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
        #confirmDialog { text-align: center; }
        #confirmDialog i { font-size: 34px; color: var(--color-accent); margin-bottom: 10px; }
        #confirmDialog p { color: var(--color-darker); font-size: 14px; margin-bottom: 25px; }
        #confirmDialog .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    </style>
    <script>
        var formPendingSubmit = null;

        function openUpdateConfirm() {
            var form = document.getElementById("updateTreatmentForm");

            if (!form.reportValidity()) {
                return;
            }

            formPendingSubmit = form;
            document.getElementById("confirmMessage").textContent = "Save these changes to this treatment type?";
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

        <h2 class="page-title">Update Treatment Type</h2>

        <%
            TreatmentType editTreatment = (TreatmentType) request.getAttribute("treatment");
        %>

        <div class="form-card">
            <form action="updateTreatment" method="post" id="updateTreatmentForm">
                <input type="hidden" name="treatmentTypeId" value="<%= editTreatment.getTreatmentTypeId() %>">

                <div class="form-group">
                    <label>Name</label>
                    <input type="text" name="name" value="<%= editTreatment.getName() %>" required>
                </div>

                <div class="form-group">
                    <label>Cost (Rs.)</label>
                    <input type="number" name="cost" step="0.01" min="0" value="<%= editTreatment.getCost() %>" required>
                </div>

                <button type="button" class="btn btn-primary" onclick="openUpdateConfirm();">Save Changes</button>
            </form>
        </div>

        <br>
        <a href="treatmentList">Back to Treatment List</a>

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
