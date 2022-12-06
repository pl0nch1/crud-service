<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Ticket"%>
<%@page import="models.Status"%>
<%@page import="models.StatusTransition"%>
<%@page import="models.Comments"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <form action="edit" method="post">
        <input type="text" name="full_name" id="full_name" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}" readonly>
        <div class="form-example">
          <label for="title">Title: </label>
          <input type="text" name="title" id="title" value="${ticket.getTitle()}">
        </div>
        <div class="form-example">
          <label for="description">Description: </label>
          <input type="text" name="description" id="description" value="${ticket.getDescription()}">
        </div>
        <div class="form-example">
          <label for="status">Status: </label>
          <select name="status" id="status">
            <c:forEach var="status" items="${statuses}">
                <c:choose>
                    <c:when test="${status.getStatusId()==ticket.getCurrentStatus().getStatusId()}">
                        <option type="text" value="${status.getStatusId()}" selected>
                            <c:out value="${status.getName()}"/>
                        </option>
                    </c:when>
                    <c:otherwise>
                        <option type="text" value="${status.getStatusId()}">
                            <c:out value="${status.getName()}"/>
                        </option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
          </select>
        </div>
        <div class="form-example">
          <label for="priority">Enter priority: </label>
          <input type="text" name="priority" id="priority" value="${ticket.getPriority()}">
        </div>
        <div class="form-example">
          <input type="submit" value="Save changes">
        </div>
    </form>

    <form action="delete" method="post">
        <div class="form-example" >
          <input type="text" name="full_name" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}" hidden>
          <input type="submit" value="Delete">
        </div>
    </form>

    <table>
        <tr>
            <th>Status from</th>
            <th>Status to</th>
            <th>Time</th>
        </tr>
        <c:forEach var="transition" items="${transitions}">
            <tr>
                <td>
                    <c:out value="${transition.getStatusFrom().getName()}"></c:out>
                </td>
                <td>
                    <c:out value="${transition.getStatusTo().getName()}"></c:out>
                </td>
                <td>
                    <c:out value="${transition.getTransitionTime()}"></c:out>
                </td>
            </tr>
        </c:forEach>
    </table>

    <table>
        <tr>
            <th>Time</th>
            <th>Author</th>
            <th>Contents</th>
        </tr>
        <c:forEach var="comment" items="${comments}">
            <tr>
                <td>
                    <c:out value="${comment.getCreationTime()}"></c:out>
                </td>
                <td>
                    <c:out value="${comment.getAuthor().getName()}"></c:out>
                </td>
                <td>
                    <c:out value="${comment.getContents()}"></c:out>
                </td>
            </tr>
        </c:forEach>
    </table>

    <a href='/BD1-1/dashboard'>На главную</a>
</html>