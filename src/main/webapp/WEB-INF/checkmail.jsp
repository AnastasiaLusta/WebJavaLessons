<%@ page import="step.learning.entities.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    User authUser = (User) request.getAttribute("AuthUser");
    String home = request.getContextPath();
    String confirmOk = (String) request.getAttribute("confirm");
    String confirmError = (String) request.getAttribute("confirmError");
%>
<div class="check-mail">
    <% if (confirmOk != null) { /* Почта подверждена - выводим только сообщение */ %>
    <h1>Почта подтверждена, можете закрыть эту страницу</h1>
    <% } else { %>
    <% if (authUser == null) { /* Пользователь не авторизован */ %>
    <h2>Авторизируйтесь (логин и пароль в верхней панели)</h2>
    <% } else if (authUser.getEmailCode() == null) { /* подтверждение не требуется */ %>
    <h2>Почта подтверждена, действий не требуется</h2>
    <h3>Если Вы хотите изменить почту, перейдите в <a href="<%=home%>/profile">личный кабинет</a></h3>
    <% } else if (authUser.getEmailCodeAttempts() > 3) { /* превышено кол-во попыток */ %>
    <h2>Превышено кол-во попыток. Измените почту в личном кабинете</h2>
    <% } else { /* подтверждение требуется */ %>
    <h1>Подтверждаем почту</h1>
    <form>
        <label>Введите код из сообщения в электронной почте
            <input type="text" name="confirm"/></label>
        <input type="submit" value="Подтвердить"/>
    </form>
    <% } %>
    <% if (confirmError != null) { %>
    <b><%= confirmError %>
    </b>
    <% } %>
    <% } %>
</div>