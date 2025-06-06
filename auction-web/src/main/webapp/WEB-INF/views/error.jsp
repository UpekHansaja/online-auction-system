<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Online Auction System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>Error</h1>
    <p>Sorry, an error occurred while processing your request.</p>
    <p>Error Code: ${errorCode}</p>
    <p>Error Message: ${errorMessage}</p>
    <a href="${pageContext.request.contextPath}/home">Return to Home</a>
</div>
</body>
</html>