<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ships</title>
</head>
<body>
<div>
    <h2>Ships</h2>
    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>name</th>
            <th>planet</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="ship" items="${ships}">
            <tr>
                <td>${ship.id}</td>
                <td>${ship.name}</td>
                <td>${ship.planet}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>