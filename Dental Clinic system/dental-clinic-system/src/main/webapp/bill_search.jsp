<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.model.Appointment, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Calculate Bill - Find Appointment</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .doctor-result-row {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius);
            padding: 16px;
            margin-bottom: 10px;
            display: flex;
            gap: 12px;
            align-items: flex-end;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Find Appointment to Bill</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <!-- step 1: search doctor -->
        <form action="bill" method="post" style="display: flex; gap: 10px; align-items: flex-end; max-width: 500px; margin-bottom: 20px;">
            <input type="hidden" name="action" value="searchDoctorForBill">
            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                <label>Doctor name</label>
                <input type="text" name="doctorName" required>
            </div>
            <button type="submit" class="btn btn-dark">Search Doctor</button>
        </form>

        <%
            List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
        %>

        <% if (doctorResults != null) { %>
            <h3>Select Doctor</h3>
            <% for (User d : doctorResults) { %>
                <form action="bill" method="post" class="doctor-result-row">
                    <input type="hidden" name="action" value="searchAppointmentsForBill">
                    <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                    <div>
                        <i class="fa fa-user-md" style="color: var(--color-accent); margin-right: 6px;"></i>
                        <strong>Dr. <%= d.getFirstName() %> <%= d.getLastName() %></strong>
                    </div>
                    <div class="form-group" style="margin-bottom: 0;">
                        <label>Date</label>
                        <input type="date" name="appointmentDate" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">Find Appointments</button>
                </form>
            <% } %>
        <% } %>

        <%
            List<Appointment> appointmentResults = (List<Appointment>) request.getAttribute("appointmentResults");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <% if (appointmentResults != null) { %>
            <h3>Appointments</h3>
            <% if (appointmentResults.isEmpty()) { %>
                <p>No appointments found for that doctor on that date.</p>
            <% } else { %>
                <table class="app-table">
                    <tr>
                        <th>Appointment #</th>
                        <th>Date/Time</th>
                        <th>Status</th>
                        <th></th>
                    </tr>
                    <% for (Appointment a : appointmentResults) { %>
                        <tr>
                            <td><%= a.getAppointmentNumber() %></td>
                            <td><%= a.getAppointmentDateTime().format(niceFormat) %></td>
                            <td><span class="badge badge-scheduled"><%= a.getStatus() %></span></td>
                            <td>
                                <form action="bill" method="post">
                                    <input type="hidden" name="action" value="calculateBill">
                                    <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>">
                                    <button type="submit" class="btn btn-primary btn-sm">Calculate Bill</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </table>
            <% } %>
        <% } %>

    </div>

</body>
</html>
