<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, com.dentalclinic.model.TreatmentType" %>
<!DOCTYPE html>
<html>
<head>
    <title>Treatment Types</title>
    <link rel="stylesheet" href="css/app-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body>

    <%@ include file="common_nav.jsp" %>

    <div class="app-container">

        <h2 class="page-title">Treatment Types</h2>

        <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 15px; margin-bottom: 20px;">

            <form action="treatmentList" method="post" style="display: flex; gap: 10px; align-items: flex-end;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label>Search by name</label>
                    <input type="text" name="searchName" style="width: 220px;" required>
                </div>
                <button type="submit" class="btn btn-dark">Search</button>
            </form>

            <a href="createTreatment" class="btn btn-primary">+ Create New Treatment Type</a>
        </div>

        <% Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult"); %>

        <% if (isSearchResult != null && isSearchResult) { %>
            <div class="alert alert-success">
                Showing results for "<%= request.getAttribute("searchedName") %>" &nbsp;
                <a href="treatmentList" style="text-decoration:underline;">Clear search</a>
            </div>
        <% } %>

        <%
            List<TreatmentType> treatments = (List<TreatmentType>) request.getAttribute("treatments");
        %>

        <% if (treatments.isEmpty()) { %>
            <p>No treatment types found.</p>
        <% } else { %>
            <table class="app-table">
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
                            <a href="updateTreatment?treatmentTypeId=<%= t.getTreatmentTypeId() %>" class="btn btn-outline btn-sm">Update</a>
                        </td>
                    </tr>
                <% } %>
            </table>
        <% } %>

        <!-- pagination - only shown for the normal (non-search) view -->
        <% if (isSearchResult == null) {
            int currentPage = (Integer) request.getAttribute("currentPage");
            int totalPages = (Integer) request.getAttribute("totalPages");
        %>
            <div class="pagination">
                <% if (currentPage > 1) { %>
                    <a href="treatmentList?page=<%= currentPage - 1 %>">&laquo; Previous</a>
                <% } %>
                <strong>Page <%= currentPage %> of <%= totalPages %></strong>
                <% if (currentPage < totalPages) { %>
                    <a href="treatmentList?page=<%= currentPage + 1 %>">Next &raquo;</a>
                <% } %>
            </div>
        <% } %>

    </div>

</body>
</html>
