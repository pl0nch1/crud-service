<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Queue"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <link rel="stylesheet" href='/BD1-1/styles.css' type="text/css">
    </head>
    <div class="main-container">
    <h1>Создание тикета</h1>
    <form action="create" method="post">
        <div class="form-example">
          <label for="description">Enter description: </label>
          <input type="text" name="description" id="description" required>
        </div>
        <div class="form-example">
          <label for="queue">Choose queue: </label>
          <select name="queue" id="queue">
            <c:forEach var="queue" items="${queues}">
                <option value="${queue.getQueueId()}">${queue.getName()}</option>
            </c:forEach>
          </select>
        </div>
        <div class="form-example">
          <label for="title">Enter title: </label>
          <input type="text" name="title" id="title" required>
        </div>
        <div class="form-submit">
          <input type="submit" value="Create ticket">
        </div>
    </form>
    <a href='/BD1-1/dashboard' class="actlink">На главную</a>
    </div>
</html>
