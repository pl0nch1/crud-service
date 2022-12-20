<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="models.Ticket"%>

<html>
    <head>
        <link rel="stylesheet" href='styles.css' type="text/css">
    </head>
    <div class="main-container">
    <h1>Тикеты</h1>
    <table class="dashboard">
        <thead>
            <th>Name</th>
            <th>Title</th>
            <th>Status</th>
            <th>Priority</th>
        </thead>
        <tbody>
        <c:forEach var="ticket" items="${tickets}">
            <tr>
                <td>
                    <c:set var="ticket_name" scope="page" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}"/>
                    <a href="/BD1-1/tickets/edit?ticket_name=${ticket_name}" class="ticket">
                        <c:out value="${ticket_name}"></c:out>
                    </a>
                </td>
                <td>
                    <c:out value="${ticket.getTitle()}"></c:out>
                </td>
                <td>
                    <c:out value="${ticket.getCurrentStatus().getName()}"></c:out>
                </td>
                <td>
                    <c:out value="${ticket.getPriority()}"></c:out>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <a href='/BD1-1/tickets/create' class="actlink">Завести тикет</a>
    <a href='/BD1-1/queue' class="actlink">Завести очередь</a>
    <a href='/BD1-1/persons' class="actlink">Просмотреть пользователей</a>
    </div>
</html>