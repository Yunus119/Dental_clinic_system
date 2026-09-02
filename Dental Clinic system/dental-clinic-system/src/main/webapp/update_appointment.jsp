<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.model.Appointment, com.dentalclinic.service.SlotInfo" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Appointment</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .current-card {
            background: var(--color-bg-light);
            border-radius: var(--radius-card);
            padding: 18px 22px;
            margin-bottom: 20px;
            display: inline-block;
        }
        #confirmDialog { text-align: center; }
        #confirmDialog i { font-size: 34px; color: var(--color-accent); margin-bottom: 10px; }
        #confirmDialog p { color: var(--color-darker); font-size: 14px; margin-bottom: 25px; }
        #confirmDialog .confirm-actions { display: flex; gap: 10px; justify-content: center; }
    </style>
    <script>
        var formPendingSubmit = null;

        function askConfirm(button, message) {
            formPendingSubmit = button.closest("form");
            document.getElementById("confirmMessage").textContent = message;
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

        <h2 class="page-title">Update Appointment</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <%
            Appointment appointment = (Appointment) request.getAttribute("appointment");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <div class="current-card">
            <strong>Current:</strong> Appointment #<%= appointment.getAppointmentNumber() %>
            &mdash; <%= appointment.getAppointmentDateTime().format(niceFormat) %>
            &mdash; <span class="badge badge-scheduled"><%= appointment.getStatus() %></span>
        </div>

        <form action="updateAppointment" method="post" style="display: flex; gap: 10px; align-items: flex-end;">
            <input type="hidden" name="action" value="loadSchedule">
            <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
            <div class="form-group" style="margin-bottom: 0;">
                <label>New Date</label>
                <input type="date" name="newDate" required>
            </div>
            <button type="submit" class="btn btn-dark">Show Schedule</button>
        </form>

        <br>

        <%
            List<SlotInfo> schedule = (List<SlotInfo>) request.getAttribute("schedule");
            String selectedDate = (String) request.getAttribute("selectedDate");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        %>

        <% if (schedule != null) { %>

            <h3>Schedule for <%= selectedDate %></h3>

            <div class="slot-grid">
                <% for (SlotInfo slot : schedule) { %>

                    <% if (slot.isAvailable()) { %>
                        <form action="updateAppointment" method="post">
                            <input type="hidden" name="action" value="saveReschedule">
                            <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
                            <input type="hidden" name="newDate" value="<%= selectedDate %>">
                            <input type="hidden" name="slotNumber" value="<%= slot.getSlotNumber() %>">
                            <input type="hidden" name="status" id="hiddenStatus_<%= slot.getSlotNumber() %>">
                            <button type="button" class="slot-available"
                                    onclick="document.getElementById('hiddenStatus_<%= slot.getSlotNumber() %>').value = document.getElementById('statusSelect').value;
                                             askConfirm(this, 'Move appointment to <%= slot.getTime().format(timeFormat) %> on <%= selectedDate %>?');">
                                <%= slot.getTime().format(timeFormat) %><br>
                                <span style="font-weight: 400; font-size: 11px;">Slot #<%= slot.getSlotNumber() %></span>
                            </button>
                        </form>
                    <% } else { %>
                        <div class="slot-taken">
                            <%= slot.getTime().format(timeFormat) %><br>
                            <span style="font-weight: 400; font-size: 11px;">Slot #<%= slot.getSlotNumber() %> - Booked</span>
                        </div>
                    <% } %>

                <% } %>
            </div>

            <br>
            <div class="form-group" style="max-width: 250px;">
                <label>Status (applied when you pick a slot above)</label>
                <select id="statusSelect">
                    <option value="SCHEDULED" <%= appointment.getStatus().equals("SCHEDULED") ? "selected" : "" %>>Scheduled</option>
                    <option value="COMPLETED" <%= appointment.getStatus().equals("COMPLETED") ? "selected" : "" %>>Completed</option>
                    <option value="CANCELLED" <%= appointment.getStatus().equals("CANCELLED") ? "selected" : "" %>>Cancelled</option>
                </select>
            </div>

        <% } %>

        <br><br>
        <a href="appointmentList">Back to Appointment List</a>

        <!-- shared custom confirm modal -->
        <dialog id="confirmDialog">
            <i class="fa fa-calendar-check-o"></i>
            <p id="confirmMessage"></p>
            <div class="confirm-actions">
                <button type="button" class="btn btn-primary" onclick="confirmYes();">Yes, Move It</button>
                <button type="button" class="btn btn-outline" onclick="confirmNo();">Cancel</button>
            </div>
        </dialog>

    </div>

</body>
</html>
