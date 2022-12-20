<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Queue"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <link rel="stylesheet" href='/BD1-1/styles.css' type="text/css">
    </head>
    <div class="main-container">
    <h1>Заведение очереди</h1>
    <form action="queue" method="post">
        <div class="form-example">
          <label for="name">Enter name: </label>
          <input type="text" name="name" id="name" required>
        </div>
        <div class="form-submit">
          <input type="submit" value="Create queue">
        </div>
    </form>

    <a href='/BD1-1/dashboard' class="actlink">На главную</a>
    </div>
</html>