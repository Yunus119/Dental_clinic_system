<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.Patient" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Choose Patient</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .step-indicator {
            font-size: 12px;
            color: var(--color-accent);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 5px;
        }
        .search-bar {
            display: flex;
            gap: 10px;
            align-items: flex-end;
            margin-bottom: 15px;
            justify-content: space-between;
            flex-wrap: wrap;
        }
        #confirmDialog {
            text-align: center;
        }
        #confirmDialog i {
            font-size: 34px;
            color: var(--color-accent);
            margin-bottom: 10px;
        }
        #confirmDialog p {
            color: var(--color-darker);
            font-size: 14px;
            margin-bottom: 25px;
        }
        #confirmDialog .confirm-actions {
            display: flex;
            gap: 10px;
            justify-content: center;
        }
    </style>
    <script>
        // holds the form that should actually submit once confirmed
        var formPendingSubmit = null;

        // intercepts the click, shows the custom confirm modal instead of the browser's native one
        function askConfirm(button, message) {
            formPendingSubmit = button.closest("form");
            document.getElementById("confirmMessage").textContent = message;
            document.getElementById("confirmDialog").showModal();
            return false;
        }

        // called when the person clicks "Yes" in the custom modal
        function confirmYes() {
            document.getElementById("confirmDialog").close();
            if (formPendingSubmit) {
                formPendingSubmit.submit();
            }
        }

        // called when the person clicks "Cancel" in the custom modal
        function confirmNo() {
            document.getElementById("confirmDialog").close();
            formPendingSubmit = null;
        }

        // validates the new patient form first, then shows the confirm modal
        function openCreatePatientConfirm() {
            var form = document.getElementById("createPatientForm");

            // runs the browser's normal required-field/pattern checks
            // shows the native validation messages if anything is missing or wrong
            if (!form.reportValidity()) {
                return;
            }

            document.getElementById("newPatientDialog").close();
            formPendingSubmit = form;
            document.getElementById("confirmMessage").textContent = "Create this patient and book the appointment?";
            document.getElementById("confirmDialog").showModal();
        }
    </script>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <div class="step-indicator">Step 3 of 3</div>
        <h2 class="page-title">Choose Patient</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <div class="search-bar">
            <form action="makeAppointment" method="post" style="display: flex; gap: 10px; align-items: flex-end;">
                <input type="hidden" name="action" value="searchPatient">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Search by name</label>
                    <input type="text" name="patientName" required>
                </div>
                <button type="submit" class="btn btn-dark">Search</button>
            </form>

            <button type="button" class="btn btn-primary" onclick="document.getElementById('newPatientDialog').showModal();">
                + Create New Patient
            </button>
        </div>

        <%
            List<Patient> patientResults = (List<Patient>) request.getAttribute("patientResults");
            String searchedName = (String) request.getAttribute("searchedName");
            Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");
        %>

        <% if (searchedName != null) { %>
            <div class="alert alert-success">
                Showing results for "<%= searchedName %>"
            </div>
        <% } %>

        <% if (patientResults != null) { %>

            <% if (patientResults.isEmpty()) { %>
                <p>No matching patient found. Use "Create New Patient" above.</p>
            <% } else { %>
                <table class="app-table">
                    <tr>
                        <th>Name</th>
                        <th>Contact</th>
                        <th>Address</th>
                        <th></th>
                    </tr>
                    <% for (Patient p : patientResults) {
                        String fullName = p.getFirstName() + " " + p.getLastName();
                    %>
                        <tr>
                            <td><%= p.getFirstName() %> <%= p.getLastName() %></td>
                            <td><%= p.getContactNumber() %></td>
                            <td><%= p.getAddress() %></td>
                            <td>
                                <form action="makeAppointment" method="post">
                                    <input type="hidden" name="action" value="selectPatientAndBook">
                                    <input type="hidden" name="patientId" value="<%= p.getPatientId() %>">
                                    <button type="button" class="btn btn-primary btn-sm"
                                            onclick="askConfirm(this, 'Book this appointment for <%= fullName %>?');">
                                        Select and Book
                                    </button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </table>
            <% } %>
        <% } %>

        <!-- pagination - only shown in browse mode, not after a search -->
        <% if (searchedName == null && currentPage != null) { %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <form action="makeAppointment" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="patientPage">
                        <input type="hidden" name="page" value="<%= currentPage - 1 %>">
                        <button type="submit" class="btn btn-outline btn-sm">&laquo; Previous</button>
                    </form>
                <% } %>
                <strong style="margin: 0 8px;">Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <form action="makeAppointment" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="patientPage">
                        <input type="hidden" name="page" value="<%= currentPage + 1 %>">
                        <button type="submit" class="btn btn-outline btn-sm">Next &raquo;</button>
                    </form>
                <% } %>
            </div>
        <% } %>

        <!-- create new patient popup -->
        <dialog id="newPatientDialog">
            <h3 style="margin-top:0;">Create New Patient</h3>
            <form action="makeAppointment" method="post" id="createPatientForm">
                <input type="hidden" name="action" value="createPatientAndBook">

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
                    <input type="text" name="address">
                </div>

                <div style="display: flex; gap: 10px;">
                    <button type="button" class="btn btn-primary" onclick="openCreatePatientConfirm();">
                        Create and Book
                    </button>
                    <button type="button" class="btn btn-outline" onclick="document.getElementById('newPatientDialog').close();">
                        Cancel
                    </button>
                </div>
            </form>
        </dialog>

        <!-- shared custom confirm modal, used for both booking actions above -->
        <dialog id="confirmDialog">
            <i class="fa fa-calendar-check-o"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-primary" onclick="confirmYes();">Yes, Book It</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Cancel</button>
            </div>
        </dialog>

    </div>

</body>
</html>
