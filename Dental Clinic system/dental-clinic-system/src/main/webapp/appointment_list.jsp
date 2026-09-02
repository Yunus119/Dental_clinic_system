<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.time.format.DateTimeFormatter,
                  com.dentalclinic.service.AppointmentListItem" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointments</title>
</head>
<body>

    <h2>Appointments</h2>

    <%
        Boolean isDoctor = (Boolean) request.getAttribute("isDoctor");
        String doctorNameFilter = (String) request.getAttribute("doctorNameFilter");
        String patientNameFilter = (String) request.getAttribute("patientNameFilter");
        String dateFilter = (String) request.getAttribute("dateFilter");
        String appointmentNumberFilter = (String) request.getAttribute("appointmentNumberFilter");
    %>

    <!-- filters - all optional -->
    <form action="appointmentList" method="get">

        <% if (!isDoctor) { %>
            <label>Doctor:</label>
            <input type="text" name="doctorName" value="<%= doctorNameFilter != null ? doctorNameFilter : "" %>">
        <% } %>

        <label>Patient:</label>
        <input type="text" name="patientName" value="<%= patientNameFilter != null ? patientNameFilter : "" %>">

        <label>Date:</label>
        <input type="date" name="date" value="<%= dateFilter != null ? dateFilter : "" %>">

        <label>Appointment #:</label>
        <input type="number" name="appointmentNumber" value="<%= appointmentNumberFilter != null ? appointmentNumberFilter : "" %>">

        <button type="submit">Filter</button>
        <a href="appointmentList">Clear</a>
    </form>

    <br>

    <%
        List<AppointmentListItem> appointments = (List<AppointmentListItem>) request.getAttribute("appointments");
        DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    %>

    <% if (appointments.isEmpty()) { %>
        <p>No appointments found.</p>
    <% } else { %>
        <table border="1" cellpadding="5">
            <tr>
                <th>#</th>
                <th>Date/Time</th>
                <th>Patient</th>
                <% if (!isDoctor) { %><th>Doctor</th><% } %>
                <th>Status</th>
                <% if (!isDoctor) { %><th>Actions</th><% } %>
            </tr>
            <% for (AppointmentListItem a : appointments) { %>
                <tr>
                    <td><%= a.getAppointmentNumber() %></td>
                    <td><%= a.getAppointmentDateTime().format(niceFormat) %></td>
                    <td><%= a.getPatientName() %></td>
                    <% if (!isDoctor) { %><td><%= a.getDoctorName() %></td><% } %>
                    <td><%= a.getStatus() %></td>
                    <% if (!isDoctor) { %>
                        <td>
                            <% if (!a.getStatus().equals("CANCELLED")) { %>
                                <a href="updateAppointment?appointmentId=<%= a.getAppointmentId() %>">Update</a> |
                                <form action="cancelAppointment" method="post" style="display:inline;">
                                    <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>">
                                    <button type="submit"
                                            onclick="return confirm('Cancel this appointment?');">
                                        Cancel
                                    </button>
                                </form>
                            <% } else { %>
                                <em>Cancelled</em>
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

    <br>
    <div>
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
                    <strong>[<%= i %>]</strong>
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

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>