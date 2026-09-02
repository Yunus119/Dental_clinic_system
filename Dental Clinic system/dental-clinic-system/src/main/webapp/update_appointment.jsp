<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.model.Appointment, com.dentalclinic.service.SlotInfo" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Appointment</title>
</head>
<body>

    <h2>Update Appointment</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>

    <%
        Appointment appointment = (Appointment) request.getAttribute("appointment");
        DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    %>

    <p><strong>Current:</strong> Appointment #<%= appointment.getAppointmentNumber() %>
       - <%= appointment.getAppointmentDateTime().format(niceFormat) %>
       - <%= appointment.getStatus() %></p>

    <!-- pick a new date to see available slots -->
    <form action="updateAppointment" method="post">
        <input type="hidden" name="action" value="loadSchedule">
        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
        <label>New Date:</label>
        <input type="date" name="newDate" required>
        <button type="submit">Show Schedule</button>
    </form>

    <br>

    <%
        List<SlotInfo> schedule = (List<SlotInfo>) request.getAttribute("schedule");
        String selectedDate = (String) request.getAttribute("selectedDate");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
    %>

    <% if (schedule != null) { %>

        <h3>Schedule for <%= selectedDate %></h3>

        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; max-width: 600px;">
            <% for (SlotInfo slot : schedule) { %>

                <% if (slot.isAvailable()) { %>
                    <form action="updateAppointment" method="post">
                        <input type="hidden" name="action" value="saveReschedule">
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
                        <input type="hidden" name="newDate" value="<%= selectedDate %>">
                        <input type="hidden" name="slotNumber" value="<%= slot.getSlotNumber() %>">
                        <input type="hidden" name="status" id="hiddenStatus_<%= slot.getSlotNumber() %>">
                        <button type="submit"
                                onclick="document.getElementById('hiddenStatus_<%= slot.getSlotNumber() %>').value = document.getElementById('statusSelect').value;
                                         return confirm('Move appointment to <%= slot.getTime().format(timeFormat) %> on <%= selectedDate %>?');"
                                style="background-color: #c8f7c5; padding: 10px; width: 100%;">
                            <%= slot.getTime().format(timeFormat) %><br>
                            (Slot #<%= slot.getSlotNumber() %>)
                        </button>
                    </form>
                <% } else { %>
                    <div style="background-color: #f0c8c8; padding: 10px; text-align: center;">
                        <%= slot.getTime().format(timeFormat) %><br>
                        (Slot #<%= slot.getSlotNumber() %>)<br>
                        <strong>Booked</strong>
                    </div>
                <% } %>

            <% } %>
        </div>

        <br>
        <label>Status (applied when you pick a slot above):</label>
        <select id="statusSelect">
            <option value="SCHEDULED" <%= appointment.getStatus().equals("SCHEDULED") ? "selected" : "" %>>Scheduled</option>
            <option value="COMPLETED" <%= appointment.getStatus().equals("COMPLETED") ? "selected" : "" %>>Completed</option>
            <option value="CANCELLED" <%= appointment.getStatus().equals("CANCELLED") ? "selected" : "" %>>Cancelled</option>
        </select>

    <% } %>

    <br><br>
    <a href="appointmentList">Back to Appointment List</a>

</body>
</html>