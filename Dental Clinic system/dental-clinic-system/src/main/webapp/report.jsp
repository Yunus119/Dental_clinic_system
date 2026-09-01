<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.service.TreatmentPopularity" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reports</title>
</head>
<body>

    <h2>Clinic Reports</h2>

    <form action="report" method="post">
        <input type="hidden" name="action" value="viewReport">
        <label>From:</label>
        <input type="date" name="startDate" required>
        <label>To:</label>
        <input type="date" name="endDate" required>
        <button type="submit">Generate Report</button>
    </form>

    <%
        Object appointmentCount = request.getAttribute("appointmentCount");
    %>

    <% if (appointmentCount != null) { %>

        <h3>Summary: <%= request.getAttribute("startDate") %> to <%= request.getAttribute("endDate") %></h3>
        <p><strong>Total Appointments:</strong> <%= appointmentCount %></p>
        <p><strong>Total Revenue:</strong> Rs. <%= request.getAttribute("revenue") %></p>

        <h3>Most Requested Treatments</h3>
        <%
            List<TreatmentPopularity> treatmentPopularity = (List<TreatmentPopularity>) request.getAttribute("treatmentPopularity");
        %>
        <% if (treatmentPopularity.isEmpty()) { %>
            <p>No treatments booked in this period.</p>
        <% } else { %>
            <table border="1" cellpadding="5">
                <tr><th>Treatment</th><th>Times Booked</th></tr>
                <% for (TreatmentPopularity t : treatmentPopularity) { %>
                    <tr>
                        <td><%= t.getTreatmentName() %></td>
                        <td><%= t.getTimesBooked() %></td>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <br>
        <form action="report" method="post">
            <input type="hidden" name="action" value="downloadCsv">
            <input type="hidden" name="startDate" value="<%= request.getAttribute("startDate") %>">
            <input type="hidden" name="endDate" value="<%= request.getAttribute("endDate") %>">
            <button type="submit">Download as CSV</button>
        </form>

    <% } %>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>