<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Help</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .help-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 28px;
            margin-bottom: 20px;
        }
        .help-card h3 {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .help-card h3 i {
            color: var(--color-accent);
        }
        .help-card ul {
            margin: 0;
            padding-left: 20px;
        }
        .help-card li {
            margin-bottom: 8px;
            font-size: 14px;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Help &amp; Instructions</h2>

        <div class="help-card">
            <h3><i class="fa fa-sign-in"></i> Logging In / Out</h3>
            <p>Enter your username and password on the login page and click Login. Click Logout in the top menu at any time to end your session.</p>
        </div>

        <% if (currentUser.getRole().equals("ADMIN")) { %>
            <div class="help-card">
                <h3><i class="fa fa-user-secret"></i> For Admins</h3>
                <ul>
                    <li><strong>Create New User:</strong> Fill in the role, name, username, email and password, then submit.</li>
                    <li><strong>User List:</strong> View, search, filter by role, update, delete, or reset the password of any user.</li>
                    <li><strong>Treatment Types:</strong> Create or update the treatments the clinic offers and their prices.</li>
                </ul>
            </div>
        <% } %>

        <% if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("RECEPTIONIST")) { %>
            <div class="help-card">
                <h3><i class="fa fa-user-md"></i> For Receptionists</h3>
                <ul>
                    <li><strong>Doctor List:</strong> Browse or search all doctors.</li>
                    <li><strong>Make Appointment:</strong> Choose a doctor, pick a date to see their available time slots, then find or create the patient to complete the booking.</li>
                    <li><strong>Calculate Bill:</strong> Search for the doctor and appointment, then calculate and print the bill.</li>
                    <li><strong>Appointment List:</strong> Search by doctor, patient, date, or appointment number. From here you can also update (reschedule) or cancel an appointment.</li>
                    <li><strong>Patient List:</strong> View and update existing patient details.</li>
                    <li><strong>Reports:</strong> Pick a date range to see appointment counts, revenue, and popular treatments, or download as CSV.</li>
                </ul>
            </div>
        <% } %>

        <% if (currentUser.getRole().equals("DOCTOR")) { %>
            <div class="help-card">
                <h3><i class="fa fa-stethoscope"></i> For Doctors</h3>
                <ul>
                    <li><strong>Appointments:</strong> See your upcoming appointments, or filter by date and patient name.</li>
                    <li><strong>Patient List:</strong> View patient details (read-only).</li>
                </ul>
            </div>
        <% } %>

        <br>
        <a href="dashboard.jsp">Back to Dashboard</a>

    </div>

</body>
</html>
