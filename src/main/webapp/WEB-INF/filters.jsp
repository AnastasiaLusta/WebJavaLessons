<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fromDemoFilter = (String) request.getAttribute("DemoFilter");
    // System.out.println( "JSP" ) ;
    // ! cast warning: List<String> users = (List<String>) request.getAttribute( "users" ) ;
    String[] users = (String[]) request.getAttribute("Users");
%>
<jsp:include page="/WEB-INF/headerfragment.jsp"/>

<h1>Сервлетные фильтры</h1>
<p>
    Фильтры (веб-фильтры, сервлетные фильтры) - сервлеты (классы для сетевых задач),
    предназначенные для реализации концепции MiddleWare: конвейерной обработки запроса
    (и ответа).
    Фильтры срабатывают до того как создаются Сервлеты (раньше). Поэтому в фильтрах
    проводят действия, необходимые для группы запросов (для всех запросов), например,
    подключение к БД, авторизацию и т.п.
</p>
<p>
    Для создания фильтра (добавления MiddleWare) необходимо:
    - Создать класс, реализовать javax.servlet.Filter
    - Три метода интерфейса отвечают за события жизненного цикла фильтра
    = init - создание (настройка) фильтра. Здесь передается ссылка на конфигурацию
    FilterConfig, эту ссылку желательно сохранить. Через нее можно получить
    сведения об окружении запуска (файловые пути и т.п.)
    = doFilter - работа фильтра, среди параметров передается ссылка на цепочку
    следующих фильтров filterChain. Фильтр может прервать дальнейшую обработку,
    если не вызовет продолжение цепочки, либо продолжить filterChain.doFilter(...)
    Код, который находится до filterChain.doFilter выполняется на "прямом" пути
    запроса; после - на "обратном"
    = destroy - разрушение фильтра.
    - Зарегистрировать фильтр: либо аннотацией, либо в web.xml. Рекомендуется - web.xml,
    т.к. при помощи аннотаций неудобно указывать порядок выполнения фильров.
</p>
<p>
    Для передачи результатов работы фильтра остальным участникам используются
    атрибуты запроса.
    <% if (fromDemoFilter == null) { %>
    <i>Запрос не проходил через DemoFilter</i>
    <% } else { %>
    <i>DemoFilter передал данные <b><%= fromDemoFilter %>
    </b></i>
    <% } %>
</p>
<p>Информация от БД: <%= request.getAttribute("Count") %>
</p>
<% if (users == null) { %>
<p>Пользователи не найдены</p>
<% } else { %>
<% for (String user : users) { %>
<span><%= user %></span> <br/>
<% } %>
<% } %>

<p>
    URL: img/glassfish.jpg /
    getRequestURI() /WebBasics/img/glassfish.jpg /WebBasics/
    getContextPath() /WebBasics /WebBasics
    getServletPath() /img/glassfish.jpg /
</p>
<p>
    Задание: создать фильтр (самый первый), который установит кодировки UTF-8
    Задание: в начале работы приложения проверить возможность подключения к БД
    если подключения нет, все запросы переводить на /static
</p>
</body>
</html>
