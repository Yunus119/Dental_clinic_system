<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Appointment, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Appointment Booked</title>
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
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <%
            Appointment booked = (Appointment) request.getAttribute("bookedAppointment");
            DateTimeFormatter niceFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        %>

        <div class="success-card">
            <i class="fa fa-check-circle"></i>
            <h2>Appointment Booked!</h2>

            <div class="detail">
                <p><strong>Appointment #:</strong> <%= booked.getAppointmentNumber() %></p>
                <p><strong>Date/Time:</strong> <%= booked.getAppointmentDateTime().format(niceFormat) %></p>
                <p><strong>Status:</strong> <%= booked.getStatus() %></p>
            </div>

            <br>
            <a href="dashboard.jsp" class="btn btn-primary">Back to Dashboard</a>
        </div>

    </div>

</body>
</html>
