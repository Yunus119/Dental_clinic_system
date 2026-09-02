<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.service.TreatmentPopularity" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reports</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .stat-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin: 20px 0 30px;
        }
        .stat-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 22px;
        }
        .stat-card i {
            font-size: 22px;
            color: var(--color-accent);
            margin-bottom: 8px;
            display: block;
        }
        .stat-card .stat-value {
            font-size: 26px;
            font-weight: 600;
            color: var(--color-darker);
        }
        .stat-card .stat-label {
            font-size: 12px;
            color: var(--color-text);
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Clinic Reports</h2>

        <form action="report" method="post" style="display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap;">
            <input type="hidden" name="action" value="viewReport">
            <div class="form-group" style="margin-bottom: 0;">
                <label>From</label>
                <input type="date" name="startDate" required>
            </div>
            <div class="form-group" style="margin-bottom: 0;">
                <label>To</label>
                <input type="date" name="endDate" required>
            </div>
            <button type="submit" class="btn btn-primary">Generate Report</button>
        </form>

        <%
            Object appointmentCount = request.getAttribute("appointmentCount");
        %>

        <% if (appointmentCount != null) { %>

            <h3 style="margin-top: 30px;">Summary: <%= request.getAttribute("startDate") %> to <%= request.getAttribute("endDate") %></h3>

            <div class="stat-cards">
                <div class="stat-card">
                    <i class="fa fa-calendar"></i>
                    <div class="stat-value"><%= appointmentCount %></div>
                    <div class="stat-label">Total Appointments</div>
                </div>
                <div class="stat-card">
                    <i class="fa fa-money"></i>
                    <div class="stat-value">Rs. <%= request.getAttribute("revenue") %></div>
                    <div class="stat-label">Total Revenue</div>
                </div>
            </div>

            <h3>Most Requested Treatments</h3>
            <%
                List<TreatmentPopularity> treatmentPopularity = (List<TreatmentPopularity>) request.getAttribute("treatmentPopularity");
            %>
            <% if (treatmentPopularity.isEmpty()) { %>
                <p>No treatments booked in this period.</p>
            <% } else { %>
                <table class="app-table">
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
                <button type="submit" class="btn btn-dark"><i class="fa fa-download"></i> Download as CSV</button>
            </form>

        <% } %>

    </div>

</body>
</html>
