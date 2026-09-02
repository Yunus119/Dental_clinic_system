<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Treatment Type</title>
</head>
<body>

    <h2>Update Treatment Type</h2>

    <%
        TreatmentType treatment = (TreatmentType) request.getAttribute("treatment");
    %>

    <form action="updateTreatment" method="post">
        <input type="hidden" name="treatmentTypeId" value="<%= treatment.getTreatmentTypeId() %>">

        <label>Name:</label>
        <input type="text" name="name" value="<%= treatment.getName() %>" required><br><br>

        <label>Cost (Rs.):</label>
        <input type="number" name="cost" step="0.01" min="0" value="<%= treatment.getCost() %>" required><br><br>

        <button type="submit">Save Changes</button>
    </form>

    <br>
    <a href="treatmentList">Back to Treatment List</a>

</body>
</html>