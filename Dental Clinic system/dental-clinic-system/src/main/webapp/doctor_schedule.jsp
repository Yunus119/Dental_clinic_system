<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.model.User, com.dentalclinic.service.SlotInfo" %>
<!DOCTYPE html>
<html>
<head>
    <title>Doctor Schedule</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <%
            User doctor = (User) request.getAttribute("doctor");
        %>

        <h2 class="page-title">Dr. <%= doctor.getFirstName() %> <%= doctor.getLastName() %>'s Schedule</h2>

        <form action="doctorSchedule" method="get" style="display: flex; gap: 10px; align-items: flex-end; margin-bottom: 25px;">
            <input type="hidden" name="doctorId" value="<%= doctor.getUserId() %>">
            <div class="form-group" style="margin-bottom: 0;">
                <label>Date</label>
                <input type="date" name="date" required>
            </div>
            <button type="submit" class="btn btn-dark">Show Schedule</button>
        </form>

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
                        <div style="background: #eaf4c8; border: 1px solid var(--color-accent); border-radius: var(--radius); padding: 12px; text-align: center; color: var(--color-dark);">
                            <%= slot.getTime().format(timeFormat) %><br>
                            <span style="font-weight: 400; font-size: 11px;">Slot #<%= slot.getSlotNumber() %> - Free</span>
                        </div>
                    <% } else { %>
                        <div class="slot-taken">
                            <%= slot.getTime().format(timeFormat) %><br>
                            <span style="font-weight: 400; font-size: 11px;">Slot #<%= slot.getSlotNumber() %> - Booked</span>
                        </div>
                    <% } %>
                <% } %>
            </div>

        <% } %>

        <br>
        <a href="doctorList">Back to Doctor List</a>

    </div>

</body>
</html>
