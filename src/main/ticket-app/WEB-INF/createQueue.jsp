<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Queue"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <c:choose>
        <c:when test="${not empty queue}">
            <h1> Queue Name <c:out value="${queue.getName()}" /> </h1>
            Id <c:out value="${queue.getQueueId()}" />
            Top count <c:out value="${queue.getTopCount()}" />
        </c:when>
        <c:otherwise>
            <h1> Not found queue with key '<c:out value="${param.key}" />' </h1>
            <c:if test="${not empty err}">
                <h2> <c:out value="${err}" /> </h2>
            </c:if>
        </c:otherwise>
    </c:choose>
    <hr>
    <form action="queue" method="post">
        <div class="form-example">
          <label for="name">Enter name: </label>
          <input type="text" name="name" id="name" required>
        </div>
        <div class="form-example">
          <input type="submit" value="Create queue">
        </div>
    </form>
</html>