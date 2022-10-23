<%@ page contentType="text/html;charset=UTF-8" %><%
    String home = request.getContextPath() ;
%>
<!doctype html >
<html>
<head>
    <meta charset="UTF-8" />
    <title>JSP basics</title>
    <link rel="stylesheet" href="<%=home%>/css/style.css" />
</head>
<body>
<jsp:include page="/WEB-INF/authfragment.jsp" />