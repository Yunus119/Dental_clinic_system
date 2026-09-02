<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.service.AppointmentListItem" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointments</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .filter-bar {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            align-items: flex-end;
            margin-bottom: 25px;
        }
        .filter-bar .form-group {
            margin-bottom: 0;
        }
        .filter-bar input {
            width: 180px;
        }
        #confirmDialog {
            text-align: center;
        }
        #confirmDialog i {
            font-size: 34px;
            color: var(--color-danger);
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
    </script>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Appointments</h2>

        <%
            Boolean isDoctor = (Boolean) request.getAttribute("isDoctor");
            String doctorNameFilter = (String) request.getAttribute("doctorNameFilter");
            String patientNameFilter = (String) request.getAttribute("patientNameFilter");
            String dateFilter = (String) request.getAttribute("dateFilter");
            String appointmentNumberFilter = (String) request.getAttribute("appointmentNumberFilter");
        %>

        <form action="appointmentList" method="get" class="filter-bar">

            <% if (!isDoctor) { %>
                <div class="form-group">
                    <label>Doctor</label>
                    <input type="text" name="doctorName" value="<%= doctorNameFilter != null ? doctorNameFilter : "" %>">
                </div>
            <% } %>

            <div class="form-group">
                <label>Patient</label>
                <input type="text" name="patientName" value="<%= patientNameFilter != null ? patientNameFilter : "" %>">
            </div>

            <div class="form-group">
                <label>Date</label>
                <input type="date" name="date" value="<%= dateFilter != null ? dateFilter : "" %>">
            </div>

            <div class="form-group">
                <label>Appointment #</label>
                <input type="number" name="appointmentNumber" value="<%= appointmentNumberFilter != null ? appointmentNumberFilter : "" %>">
            </div>

            <button type="submit" class="btn btn-dark">Filter</button>
            <a href="appointmentList" class="btn btn-outline">Clear</a>
        </form>

        <%
            List<AppointmentListItem> appointments = (List<AppointmentListItem>) request.getAttribute("appointments");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <% if (appointments.isEmpty()) { %>
            <p>No appointments found.</p>
        <% } else { %>
            <table class="app-table">
                <tr>
                    <th>#</th>
                    <th>Date/Time</th>
                    <th>Patient</th>
                    <% if (!isDoctor) { %><th>Doctor</th><% } %>
                    <th>Status</th>
                    <% if (!isDoctor) { %><th>Actions</th><% } %>
                </tr>
                <% for (AppointmentListItem a : appointments) {
                    String badgeClass = a.getStatus().equals("SCHEDULED") ? "badge-scheduled"
                            : a.getStatus().equals("COMPLETED") ? "badge-completed" : "badge-cancelled";
                %>
                    <tr>
                        <td><%= a.getAppointmentNumber() %></td>
                        <td><%= a.getAppointmentDateTime().format(niceFormat) %></td>
                        <td><%= a.getPatientName() %></td>
                        <% if (!isDoctor) { %><td><%= a.getDoctorName() %></td><% } %>
                        <td><span class="badge <%= badgeClass %>"><%= a.getStatus() %></span></td>
                        <% if (!isDoctor) { %>
                            <td>
                                <% if (!a.getStatus().equals("CANCELLED")) { %>
                                    <a href="updateAppointment?appointmentId=<%= a.getAppointmentId() %>" class="btn btn-outline btn-sm">Update</a>
                                    <form action="cancelAppointment" method="post" style="display:inline;">
                                        <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>">
                                        <button type="button" class="btn btn-danger btn-sm"
                                                onclick="askConfirm(this, 'Cancel this appointment?');">
                                            Cancel
                                        </button>
                                    </form>
                                <% } else { %>
                                    <span class="text-muted">Cancelled</span>
                                <% } %>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- numbered pagination -->
        <%
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");

            StringBuilder filterQuery = new StringBuilder();
            if (doctorNameFilter != null && !doctorNameFilter.isBlank()) filterQuery.append("&doctorName=").append(doctorNameFilter);
            if (patientNameFilter != null && !patientNameFilter.isBlank()) filterQuery.append("&patientName=").append(patientNameFilter);
            if (dateFilter != null && !dateFilter.isBlank()) filterQuery.append("&date=").append(dateFilter);
            if (appointmentNumberFilter != null && !appointmentNumberFilter.isBlank()) filterQuery.append("&appointmentNumber=").append(appointmentNumberFilter);
            String filters = filterQuery.toString();
        %>

        <div class="pagination">
            <% if (currentPage > 1) { %>
                <a href="appointmentList?page=1<%= filters %>">&laquo;&laquo;</a>
                <a href="appointmentList?page=<%= currentPage - 1 %><%= filters %>">&laquo;</a>
            <% } %>

            <%
                int windowStart = Math.max(1, currentPage - 2);
                int windowEnd = Math.min(totalPages, currentPage + 2);

                if (windowStart > 1) { %>
                    <a href="appointmentList?page=1<%= filters %>">1</a>
                    <% if (windowStart > 2) { %> ... <% }
                }

                for (int i = windowStart; i <= windowEnd; i++) {
                    if (i == currentPage) { %>
                        <strong><%= i %></strong>
                    <% } else { %>
                        <a href="appointmentList?page=<%= i %><%= filters %>"><%= i %></a>
                    <% }
                }

                if (windowEnd < totalPages) {
                    if (windowEnd < totalPages - 1) { %> ... <% } %>
                    <a href="appointmentList?page=<%= totalPages %><%= filters %>"><%= totalPages %></a>
            <% } %>

            <% if (currentPage < totalPages) { %>
                <a href="appointmentList?page=<%= currentPage + 1 %><%= filters %>">&raquo;</a>
                <a href="appointmentList?page=<%= totalPages %><%= filters %>">&raquo;&raquo;</a>
            <% } %>
        </div>

        <!-- shared custom confirm modal, used for the Cancel action -->
        <dialog id="confirmDialog">
            <i class="fa fa-exclamation-triangle"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-danger" onclick="confirmYes();">Yes, Cancel It</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Go Back</button>
            </div>
        </dialog>

    </div>

</body>
</html>
