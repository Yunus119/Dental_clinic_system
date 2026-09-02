<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Treatment Type</title>
</head>
<body>

    <h2>Create Treatment Type</h2>

    <form action="createTreatment" method="post">

        <label>Name:</label>
        <input type="text" name="name" required><br><br>

        <label>Cost (Rs.):</label>
        <input type="number" name="cost" step="0.01" min="0" required><br><br>

        <button type="submit">Create</button>
    </form>

    <br>
    <a href="treatmentList">Back to Treatment List</a>

</body>
</html>