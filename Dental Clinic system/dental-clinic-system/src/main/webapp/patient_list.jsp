<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Patient List</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script>
        var formPendingSubmit = null;

        // validates the new patient form first, then shows the confirm modal
        function openCreatePatientConfirm() {
            var form = document.getElementById("createPatientForm");

            if (!form.reportValidity()) {
                return;
            }

            document.getElementById("newPatientDialog").close();
            formPendingSubmit = form;
            document.getElementById("confirmMessage").textContent = "Create this new patient?";
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
    <style>
        #confirmDialog { text-align: center; }
        #confirmDialog i { font-size: 34px; color: var(--color-accent); margin-bottom: 10px; }
        #confirmDialog p { color: var(--color-darker); font-size: 14px; margin-bottom: 25px; }
        #confirmDialog .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">All Patients</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <%
            // currentUser already provided by common_nav.jsp - do not redeclare
            boolean canEdit = !currentUser.getRole().equals("DOCTOR");
        %>

        <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 15px; margin-bottom: 20px;">

            <form action="patientList" method="post" style="display: flex; gap: 10px; align-items: flex-end;">
                <input type="hidden" name="action" value="searchPatient">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Search by name</label>
                    <input type="text" name="searchName" required>
                </div>
                <button type="submit" class="btn btn-dark">Search</button>
            </form>

            <% if (canEdit) { %>
                <button type="button" class="btn btn-primary" onclick="document.getElementById('newPatientDialog').showModal();">
                    + Create New Patient
                </button>
            <% } %>
        </div>

        <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

        <% if (isSearchResult != null && isSearchResult) { %>
            <div class="alert alert-success">
                Showing results for "<%= request.getAttribute("searchedName") %>" &nbsp;
                <a href="patientList" style="text-decoration:underline;">Clear search</a>
            </div>
        <% } %>

        <%
            List<Patient> patients = (List<Patient>) request.getAttribute("patients");
        %>

        <% if (patients.isEmpty()) { %>
            <p>No patients found.</p>
        <% } else { %>
            <table class="app-table">
                <tr>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Address</th>
                    <% if (canEdit) { %><th>Actions</th><% } %>
                </tr>
                <% for (Patient p : patients) { %>
                    <tr>
                        <td><%= p.getFirstName() %> <%= p.getLastName() %></td>
                        <td><%= p.getContactNumber() %></td>
                        <td><%= p.getAddress() %></td>
                        <% if (canEdit) { %>
                            <td>
                                <a href="updatePatient?patientId=<%= p.getPatientId() %>" class="btn btn-outline btn-sm">Update</a>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- pagination - only shown for the normal (non-search) view -->
        <% if (isSearchResult == null) {
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");
        %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="patientList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
                <% } %>
                <strong>Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <a href="patientList?page=<%= currentPage + 1 %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>

        <!-- create new patient popup -->
        <dialog id="newPatientDialog">
            <h3 style="margin-top:0;">Create New Patient</h3>
            <form action="patientList" method="post" id="createPatientForm">
                <input type="hidden" name="action" value="createPatient">

                <div class="form-group">
                    <label>First Name</label>
                    <input type="text" name="firstName" pattern="[a-zA-Z\s'-]+" title="Letters only" required>
                </div>

                <div class="form-group">
                    <label>Last Name</label>
                    <input type="text" name="lastName" pattern="[a-zA-Z\s'-]+" title="Letters only" required>
                </div>

                <div class="form-group">
                    <label>Contact Number</label>
                    <input type="text" name="contactNumber" pattern="(0\d{9}|\+94\d{9})"
                           title="Format: 0771234567 or +94771234567" maxlength="13"
                           placeholder="0771234567 or +94771234567" required>
                </div>

                <div class="form-group">
                    <label>Address</label>
                    <input type="text" name="address" required>
                </div>

                <div style="display: flex; gap: 10px;">
                    <button type="button" class="btn btn-primary" onclick="openCreatePatientConfirm();">
                        Create Patient
                    </button>
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('newPatientDialog').close();">
                        Cancel
                    </button>
                </div>
            </form>
        </dialog>

        <!-- shared custom confirm modal -->
        <dialog id="confirmDialog">
            <i class="fa fa-user-plus"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-primary" onclick="confirmYes();">Yes, Create</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Cancel</button>
            </div>
        </dialog>

    </div>

</body>
</html>
