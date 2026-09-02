<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Bill, com.dentalclinic.model.Appointment,
                  com.dentalclinic.model.Patient, com.dentalclinic.model.User,
                  com.dentalclinic.model.TreatmentType, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bill</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .bill-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 35px;
            max-width: 450px;
        }
        .bill-card .bill-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid var(--color-border);
            font-size: 14px;
        }
        .bill-card .bill-row:last-of-type {
            border-bottom: none;
        }
        .bill-card .bill-row strong {
            color: var(--color-darker);
        }
        .bill-card .amount-row {
            font-size: 20px;
            font-weight: 600;
            color: var(--color-darker);
            padding-top: 15px;
        }

        /* hide navigation and buttons when actually printing */
        @media print {
            .app-nav, .app-topbar, .no-print { display: none !important; }
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Bill</h2>

        <%
            Bill bill = (Bill) request.getAttribute("bill");
            Appointment appointment = (Appointment) request.getAttribute("appointment");
            Patient patient = (Patient) request.getAttribute("patient");
            User doctor = (User) request.getAttribute("doctor");
            TreatmentType treatment = (TreatmentType) request.getAttribute("treatment");
            User receptionist = (User) request.getAttribute("receptionist");
            Boolean printed = (Boolean) request.getAttribute("printed");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <div class="bill-card">
            <div class="bill-row"><span>Bill ID</span><strong>#<%= bill.getBillId() %></strong></div>
            <div class="bill-row"><span>Appointment Number</span><strong><%= appointment.getAppointmentNumber() %></strong></div>
            <div class="bill-row"><span>Date/Time</span><strong><%= appointment.getAppointmentDateTime().format(niceFormat) %></strong></div>
            <div class="bill-row"><span>Patient</span><strong><%= patient.getFirstName() %> <%= patient.getLastName() %></strong></div>
            <div class="bill-row"><span>Doctor</span><strong>Dr. <%= doctor.getFirstName() %> <%= doctor.getLastName() %></strong></div>
            <div class="bill-row"><span>Treatment</span><strong><%= treatment.getName() %></strong></div>
            <div class="bill-row"><span>Billed by</span><strong><%= receptionist.getFirstName() %> <%= receptionist.getLastName() %></strong></div>
            <div class="bill-row amount-row"><span>Amount</span><span>Rs. <%= bill.getAmount() %></span></div>
        </div>

        <br class="no-print">
        <form action="bill" method="post" class="no-print" style="display:inline;">
            <input type="hidden" name="action" value="printBill">
            <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
            <button type="submit" class="btn btn-primary">Print Bill</button>
        </form>

        <br class="no-print"><br class="no-print">
        <a href="dashboard.jsp" class="no-print">Back to Dashboard</a>

        <% if (printed != null && printed) { %>
            <script>
                window.print();
            </script>
        <% } %>

    </div>

</body>
</html>
