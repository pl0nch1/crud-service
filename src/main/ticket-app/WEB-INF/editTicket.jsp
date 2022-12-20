<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Ticket"%>
<%@page import="models.Status"%>
<%@page import="models.StatusTransition"%>
<%@page import="models.Comment"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <link rel="stylesheet" href='/BD1-1/styles.css' type="text/css">
    </head>

    <div class="main-container">
    <h1> Тикет <c:out value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}"/></h1>
    <form action="edit" method="post">
        <input type="text" name="full_name" id="full_name" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}" hidden>
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
        <div class="form-submit">
          <input type="submit" value="Save changes">
        </div>
    </form>

    <form action="delete" method="post">
        <div class="form-submit" >
          <input type="text" name="full_name" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}" hidden>
          <input type="submit" value="Delete">
        </div>
    </form>

    <h2>
        История тикета
    </h2>

    <table>
        <thead>
            <th>Status from</th>
            <th>Status to</th>
            <th>Time</th>
        </thead>
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
    <hr>
    <h3>Комменатрии</h3>

    <div class="comments-container">
        <c:forEach var="comment" items="${comments}">
            <div class="comment-container">
                <div class="comment-info">
                    <span class="comment-time">
                        <c:out value="${comment.getCreationTime()}"></c:out>
                    </span>
                    <span class="comment-author">
                        <c:out value="${comment.getAuthor().getLastName()} ${comment.getAuthor().getFirstName()}"></c:out>
                    </span>
                </div>
                <span class="comment-content">
                    <c:out value="${comment.getContents()}"></c:out>
                </span>
            </div>
        </c:forEach>
    </div>

    <form action="/BD1-1/comments/create" method="post">
        <input type="text" name="full_name" value="${ticket.getQueue().getName()}-${ticket.getLocalTicketId()}" hidden>
        <div class="form-example">
             <label for="status">Author </label>
             <select name="person_id" id="person_id">
               <c:forEach var="person" items="${persons}">
                   <option type="text" value="${person.getPersonId()}">
                       <c:out value="${person.getLastName()} ${person.getFirstName()}"/>
                   </option>
               </c:forEach>
             </select>
        </div>

        <div class="form-example">
            <input type="text" name="contents" id="contents">
        </div>
        <div class="form-submit">
            <input type="submit" value="Post comment">
        </div>
    </form>

    <a href='/BD1-1/dashboard' class="actlink">На главную</a>
    </div>
</html>