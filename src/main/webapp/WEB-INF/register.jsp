<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String loginInput = (String) request.getAttribute("loginInput");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<html>
<head>
    <title>Registration page</title>
</head>
<body style="text-align: center; position: center; align-content: center">
<form method="post">
    <div class="form-group">
        <label for="loginInput">Login: </label>
        <input type="text" class="form-control" name="loginInput" id="loginInput" placeholder="Enter login...">
    </div>
    <br>
    <div class="form-group">
        <label for="passwordInput">Password: </label>
        <input type="password" class="form-control" name="passwordInput" id="passwordInput" placeholder="Password...">
    </div>
    <br>
    <div class="form-group">
        <label for="passwordInputRep">Repeat password: </label>
        <input type="password" class="form-control" name="passwordInputRep" id="passwordInputRep" placeholder="Confirm Password...">
    </div>
    <br>
    <button type="submit" class="btn btn-primary">Submit</button>
</form>
<p>
    <% if (loginInput != null) { %>
    Welcome, <%= loginInput %>
    <% } %>
</p>
<jsp:include page="footer.jsp"/>
</body>
</html>
