<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Person"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <link rel="stylesheet" href='styles.css' type="text/css">
    </head>

    <div class="main-container">
    <h1>Просмотр сотрудников</h1>
    <table>
            <thead>
                <th>First name</th>
                <th>Last name</th>
                <th>Email</th>
            </thead>
            <c:forEach var="person" items="${persons}">
                <tr>
                    <td>
                        <c:out value="${person.getFirstName()}"/>
                    </td>
                    <td>
                        <c:out value="${person.getLastName()}"/>
                    </td>
                    <td>
                        <c:out value="${person.getMail()}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>

    <a href='/BD1-1/dashboard' class="actlink">На главную</a>
    <a href='/BD1-1/person/create' class="actlink">Завести пользователя</a>
    </div>
</html>
