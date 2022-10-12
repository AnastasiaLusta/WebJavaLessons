<%--
  Created by IntelliJ IDEA.
  User: anastasialusta
  Date: 12.10.2022
  Time: 20:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registration page</title>
</head>
<body  style="text-align: center; position: center; align-content: center">
<form>
    <div class="form-group">
        <label for="exampleInputEmail1">Email address</label>
        <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
               placeholder="Enter email">
    </div>
    <br>
    <div class="form-group">
        <label for="exampleInputPassword1">Password</label>
        <input type="password" class="form-control" id="exampleInputPassword1" placeholder="Password">
    </div>
    <br>
    <div class="form-group form-check">
        <input type="checkbox" class="form-check-input" id="exampleCheck1">
        <label class="form-check-label" for="exampleCheck1">Check me out</label>
    </div>
    <br>
    <button type="submit" class="btn btn-primary">Submit</button>
</form>
<jsp:include page="WEB-INF/footer.jsp"/>
</body>
</html>
