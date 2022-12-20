<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="models.Queue"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <link rel="stylesheet" href='/BD1-1/styles.css' type="text/css">
    </head>

    <div class="main-container">
    <h1>Регистрация сотрудника</h1>
    <form action="create" method="post">
        <div class="form-example">
          <label for="fname">First name: </label>
          <input type="text" name="fname" id="fname" required>
        </div>
        <div class="form-example">
          <label for="lname">First name: </label>
          <input type="text" name="lname" id="lname" required>
        </div>
        <div class="form-example">
          <label for="mail">E-mail: </label>
          <input type="text" name="mail" id="mail">
        </div>
        <div class="form-submit">
          <input type="submit" value="Create person">
        </div>
    </form>

    <a href='/BD1-1/dashboard' class="actlink">На главную</a>
    </div>
</html>
