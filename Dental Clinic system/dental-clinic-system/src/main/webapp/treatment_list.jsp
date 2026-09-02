<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Treatment Types</title>
</head>
<body>

    <h2>Treatment Types</h2>

    <p><a href="createTreatment">+ Create New Treatment Type</a></p>

    <%
        List<TreatmentType> treatments = (List<TreatmentType>) request.getAttribute("treatments");
    %>

    <table border="1" cellpadding="5">
        <tr>
            <th>Name</th>
            <th>Cost</th>
            <th>Actions</th>
        </tr>
        <% for (TreatmentType t : treatments) { %>
            <tr>
                <td><%= t.getName() %></td>
                <td>Rs. <%= t.getCost() %></td>
                <td>
                    <a href="updateTreatment?treatmentTypeId=<%= t.getTreatmentTypeId() %>">Update</a>
                </td>
            </tr>
        <% } %>
    </table>

    <br>
    <a href="dashboard.jsp">Back to Dashboard</a>

</body>
</html>