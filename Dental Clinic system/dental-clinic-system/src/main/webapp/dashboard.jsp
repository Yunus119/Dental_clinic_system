<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .quick-links {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }
        .quick-link-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 24px;
            transition: all 0.25s ease-in-out;
            display: block;
        }
        .quick-link-card:hover {
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            transform: translateY(-3px);
            color: var(--color-darker);
        }
        .quick-link-card i {
            font-size: 26px;
            color: var(--color-accent);
            margin-bottom: 12px;
            display: block;
        }
        .quick-link-card h4 {
            margin: 0;
            font-size: 15px;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Welcome, <%= currentUser.getFirstName() %></h2>

        <div class="quick-links">

            <% if (currentUser.getRole().equals("ADMIN")) { %>
                <a href="createUser" class="quick-link-card">
                    <i class="fa fa-user-plus"></i>
                    <h4>Create New User</h4>
                </a>
                <a href="userList" class="quick-link-card">
                    <i class="fa fa-users"></i>
                    <h4>User List</h4>
                </a>
                <a href="treatmentList" class="quick-link-card">
                    <i class="fa fa-medkit"></i>
                    <h4>Treatment Types</h4>
                </a>
            <% } %>

            <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
                <a href="doctorList" class="quick-link-card">
                    <i class="fa fa-user-md"></i>
                    <h4>Doctor List</h4>
                </a>
                <a href="makeAppointment" class="quick-link-card">
                    <i class="fa fa-calendar-plus-o"></i>
                    <h4>Make Appointment</h4>
                </a>
                <a href="bill" class="quick-link-card">
                    <i class="fa fa-file-text-o"></i>
                    <h4>Calculate Bill</h4>
                </a>
                <a href="report" class="quick-link-card">
                    <i class="fa fa-bar-chart"></i>
                    <h4>Reports</h4>
                </a>
            <% } %>

            <a href="appointmentList" class="quick-link-card">
                <i class="fa fa-calendar"></i>
                <h4>Appointments</h4>
            </a>

            <a href="patientList" class="quick-link-card">
                <i class="fa fa-user-md"></i>
                <h4>Patient List</h4>
            </a>

            <a href="help" class="quick-link-card">
                <i class="fa fa-question-circle"></i>
                <h4>Help</h4>
            </a>

        </div>

    </div>

</body>
</html>
