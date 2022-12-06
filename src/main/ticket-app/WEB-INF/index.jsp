<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="models.Ticket"%>

<html>
    <table>
        <tr>
            <th>Name</th>
            <th>Title</th>
            <th>Status</th>
            <th>Priority</th>
        </tr>
        <c:forEach var="ticket" items="${tickets}">
            <tr>
                <td>
                    <c:set var="ticket_name" scope="page" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}"/>
                    <a href="/BD1-1/tickets/edit?ticket_name=${ticket_name}">
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
    </table>
    <a href='/BD1-1/tickets/create'>Завести тикет</a>
</html>