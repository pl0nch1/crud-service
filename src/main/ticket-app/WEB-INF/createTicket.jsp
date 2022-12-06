<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Queue"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <form action="create" method="post">
        <div class="form-example">
          <label for="description">Enter description: </label>
          <input type="text" name="description" id="description" required>
        </div>
        <div class="form-example">
          <label for="queue">Choose queue: </label>
          <select name="queue" id="queue">
            <c:forEach var="queue" items="${queues}">
                <option value="${queue.getQueueId()}"><c:out value="${queue.getName()}">></c:out></option>
            </c:forEach>
          </select>
        </div>
        <div class="form-example">
          <label for="title">Enter title: </label>
          <input type="text" name="title" id="title" required>
        </div>
        <div class="form-example">
          <input type="submit" value="Create ticket">
        </div>
    </form>
</html>
