<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.service.SlotInfo, com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Schedule</title>
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
        .treatment-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 20px;
            max-width: 350px;
            margin-bottom: 25px;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <div class="step-indicator">Step 2 of 3</div>
        <h2 class="page-title">Pick a Time Slot</h2>

        <form action="makeAppointment" method="post" style="display: flex; gap: 10px; align-items: flex-end; margin-bottom: 25px;">
            <input type="hidden" name="action" value="loadSchedule">
            <div class="form-group" style="margin-bottom: 0;">
                <label>Date</label>
                <input type="date" name="appointmentDate" required>
            </div>
            <button type="submit" class="btn btn-dark">Show Schedule</button>
        </form>

        <%
            List<SlotInfo> schedule = (List<SlotInfo>) request.getAttribute("schedule");
            List<TreatmentType> treatmentTypes = (List<TreatmentType>) request.getAttribute("treatmentTypes");
            String selectedDate = (String) request.getAttribute("selectedDate");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        %>

        <% if (schedule != null) { %>

            <div class="treatment-card">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Select Treatment Type</label>
                    <select id="treatmentTypeId">
                        <% for (TreatmentType t : treatmentTypes) { %>
                            <option value="<%= t.getTreatmentTypeId() %>">
                                <%= t.getName() %> - Rs. <%= t.getCost() %>
                            </option>
                        <% } %>
                    </select>
                </div>
            </div>

            <h3>Schedule for <%= selectedDate %></h3>

            <div class="slot-grid">
                <% for (SlotInfo slot : schedule) { %>

                    <% if (slot.isAvailable()) { %>
                        <form action="makeAppointment" method="post">
                            <input type="hidden" name="action" value="selectSlot">
                            <input type="hidden" name="appointmentDate" value="<%= selectedDate %>">
                            <input type="hidden" name="slotNumber" value="<%= slot.getSlotNumber() %>">
                            <input type="hidden" name="treatmentTypeId" id="hiddenTreatment_<%= slot.getSlotNumber() %>">
                            <button type="submit"
                                    onclick="document.getElementById('hiddenTreatment_<%= slot.getSlotNumber() %>').value = document.getElementById('treatmentTypeId').value;"
                                    class="slot-available">
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

        <% } %>

    </div>

</body>
</html>
