<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.Bill, com.dentalclinic.model.Appointment,
                  com.dentalclinic.model.Patient, com.dentalclinic.model.User,
                  com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bill</title>
</head>
<body>

    <h2>Dental Clinic - Bill</h2>

    <%
        Bill bill = (Bill) request.getAttribute("bill");
        Appointment appointment = (Appointment) request.getAttribute("appointment");
        Patient patient = (Patient) request.getAttribute("patient");
        User doctor = (User) request.getAttribute("doctor");
        TreatmentType treatment = (TreatmentType) request.getAttribute("treatment");
        User receptionist = (User) request.getAttribute("receptionist");
        Boolean printed = (Boolean) request.getAttribute("printed");
    %>

    <p><strong>Bill ID:</strong> <%= bill.getBillId() %></p>
    <p><strong>Appointment Number:</strong> <%= appointment.getAppointmentNumber() %></p>
    <p><strong>Date/Time:</strong> <%= appointment.getAppointmentDateTime() %></p>
    <p><strong>Patient:</strong> <%= patient.getFirstName() %> <%= patient.getLastName() %></p>
    <p><strong>Doctor:</strong> Dr. <%= doctor.getFirstName() %> <%= doctor.getLastName() %></p>
    <p><strong>Treatment:</strong> <%= treatment.getName() %></p>
    <p><strong>Amount:</strong> Rs. <%= bill.getAmount() %></p>
    <p><strong>Billed by:</strong> <%= receptionist.getFirstName() %> <%= receptionist.getLastName() %></p>

    <form action="bill" method="post">
        <input type="hidden" name="action" value="printBill">
        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId() %>">
        <button type="submit">Print Bill</button>
    </form>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

    <% if (printed != null && printed) { %>
        <script>
            window.print();
        </script>
    <% } %>

</body>
</html>