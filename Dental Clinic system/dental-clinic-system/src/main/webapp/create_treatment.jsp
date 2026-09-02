<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Treatment Type</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .form-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 35px;
            max-width: 420px;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Create Treatment Type</h2>

        <div class="form-card">
            <form action="createTreatment" method="post">

                <div class="form-group">
                    <label>Name</label>
                    <input type="text" name="name" required>
                </div>

                <div class="form-group">
                    <label>Cost (Rs.)</label>
                    <input type="number" name="cost" step="0.01" min="0" required>
                </div>

                <button type="submit" class="btn btn-primary">Create</button>
            </form>
        </div>

        <br>
        <a href="treatmentList">Back to Treatment List</a>

    </div>

</body>
</html>
