<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.service.SlotInfo, com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Schedule</title>
</head>
<body>

    <h2>Step 2: Pick a Time Slot</h2>

    <form action="makeAppointment" method="post">
        <input type="hidden" name="action" value="loadSchedule">
        <label>Date:</label>
        <input type="date" name="appointmentDate" required>
        <button type="submit">Show Schedule</button>
    </form>

    <br>

    <%
        List<SlotInfo> schedule = (List<SlotInfo>) request.getAttribute("schedule");
        List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
        String selectedDate = (String) request.getAttribute("selectedDate");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
    %>

    <% if (schedule != null) { %>

        <h3>Select Treatment Type</h3>
        <select id="treatmentTypeId">
            <% for (TreatmentType t : treatmentTypes) { %>
                <option value="<%= t.getTreatmentTypeId() %>">
                    <%= t.getName() %> - Rs. <%= t.getCost() %>
                </option>
            <% } %>
        </select>

        <h3>Schedule for <%= selectedDate %></h3>

        <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; max-width: 600px;">
            <% for (SlotInfo slot : schedule) { %>

                <% if (slot.isAvailable()) { %>
                    <form action="makeAppointment" method="post">
                        <input type="hidden" name="action" value="selectSlot">
                        <input type="hidden" name="appointmentDate" value="<%= selectedDate %>">
                        <input type="hidden" name="slotNumber" value="<%= slot.getSlotNumber() %>">
                        <input type="hidden" name="treatmentTypeId" id="hiddenTreatment_<%= slot.getSlotNumber() %>">
                        <button type="submit"
                                onclick="document.getElementById('hiddenTreatment_<%= slot.getSlotNumber() %>').value = document.getElementById('treatmentTypeId').value;"
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

    <% } %>

</body>
</html>