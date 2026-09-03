<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Appointment, com.dentalclinic.model.User,
                  com.dentalclinic.model.Patient, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointment Updated</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .success-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 40px;
            max-width: 420px;
            text-align: center;
        }
        .success-card i {
            font-size: 50px;
            color: var(--color-accent);
            margin-bottom: 15px;
        }
        .success-card .detail {
            text-align: left;
            background: var(--color-bg-light);
            border-radius: var(--radius);
            padding: 16px;
            margin-top: 20px;
            font-size: 13px;
        }
        .success-card .detail p {
            margin: 6px 0;
        }
        /* hide nav and buttons when actually printing */
        @media print {
            .app-nav, .app-topbar, .no-print { display: none !important; }
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <%
            Appointment updated = (Appointment) request.getAttribute("updatedAppointment");
            User doctor = (User) request.getAttribute("doctor");
            Patient patient = (Patient) request.getAttribute("patient");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <div class="success-card">
            <i class="fa fa-check-circle"></i>
            <h2>Appointment Updated!</h2>

            <div class="detail">
                <p><strong>Appointment #:</strong> <%= updated.getAppointmentNumber() %></p>
                <p><strong>Date/Time:</strong> <%= updated.getAppointmentDateTime().format(niceFormat) %></p>
                <p><strong>Status:</strong> <%= updated.getStatus() %></p>
                <p><strong>Doctor:</strong> Dr. <%= doctor.getFirstName() %> <%= doctor.getLastName() %></p>
                <p><strong>Patient:</strong> <%= patient.getFirstName() %> <%= patient.getLastName() %></p>
                <p><strong>Contact:</strong> <%= patient.getContactNumber() %></p>
            </div>

            <br class="no-print">
            <button type="button" class="btn btn-dark no-print" onclick="window.print();">Print Appointment Details</button>
            <br class="no-print"><br class="no-print">
            <a href="appointmentList" class="btn btn-primary no-print">Back to Appointment List</a>
        </div>

    </div>

</body>
</html>
