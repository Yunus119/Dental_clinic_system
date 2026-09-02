<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Make Appointment - Choose Doctor</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        .doctor-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 14px;
            margin-top: 20px;
        }
        .doctor-card {
            background: var(--color-white);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-card);
            padding: 20px;
            text-align: left;
            width: 100%;
            cursor: pointer;
            transition: all 0.25s ease-in-out;
        }
        .doctor-card:hover {
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            transform: translateY(-3px);
        }
        .doctor-card i {
            font-size: 22px;
            color: var(--color-accent);
            margin-bottom: 8px;
            display: block;
        }
        .doctor-card strong {
            display: block;
            font-size: 15px;
            color: var(--color-darker);
        }
        .step-indicator {
            font-size: 12px;
            color: var(--color-accent);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <div class="step-indicator">Step 1 of 3</div>
        <h2 class="page-title">Choose a Doctor</h2>

        <form action="makeAppointment" method="post" style="display: flex; gap: 10px; align-items: flex-end; max-width: 500px; margin-bottom: 10px;">
            <input type="hidden" name="action" value="searchDoctor">
            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                <label>Search by name</label>
                <input type="text" name="doctorName" placeholder="Leave blank to browse all">
            </div>
            <button type="submit" class="btn btn-dark">Search</button>
        </form>

        <%
            List<User> doctorResults = (List<User>) request.getAttribute("doctorResults");
        %>

        <% if (doctorResults != null && !doctorResults.isEmpty()) { %>
            <div class="doctor-grid">
                <% for (User d : doctorResults) { %>
                    <form action="makeAppointment" method="post">
                        <input type="hidden" name="action" value="selectDoctor">
                        <input type="hidden" name="doctorId" value="<%= d.getUserId() %>">
                        <button type="submit" class="doctor-card">
                            <i class="fa fa-user-md"></i>
                            <strong>Dr. <%= d.getFirstName() %> <%= d.getLastName() %></strong>
                            <span class="text-muted"><%= d.getEmail() %></span>
                        </button>
                    </form>
                <% } %>
            </div>
        <% } else if (doctorResults != null) { %>
            <p>No matching doctor found.</p>
        <% } %>

        <!-- pagination - only shown when browsing (not after a search) -->
        <%
            Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");
        %>
        <% if (currentPage != null) { %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="makeAppointment?page=<%= currentPage - 1 %>">&laquo; Previous</a>
                <% } %>
                <strong>Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <a href="makeAppointment?page=<%= currentPage + 1 %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>

    </div>

</body>
</html>
