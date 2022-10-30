<%@ page import="step.learning.entities.User" %>
<%@ page contentType="text/html;charset=UTF-8" %><%
    User authUser = (User) request.getAttribute( "AuthUser" ) ;
    String home = request.getContextPath() ;
%>
<div class="user-profile">
    <h1>Кабинет пользователя</h1>
    <img class="profile-avatar"
         src="<%=home%>/image/<%=authUser.getAvatar()%>"
         alt="<%=authUser.getLogin()%>" />

    <fieldset class="profile-fieldset">
        <legend>Возможно для изменения</legend>
        <p class="profile-name">
            <span>Имя:</span> <b data-field-name="name"><%= authUser.getName() %></b>
        </p>
        <p class="profile-name">
            <span>Логин:</span> <b data-field-name="login"><%= authUser.getLogin() %></b>
        </p>
        <p class="profile-fieldset-avatar">
            <span>Картинка:</span>
            <input type="file" id="avatar-input" alt="avatar-input" />
            <input type="button" value="Сохранить" id="avatar-save-button" />
        </p>
    </fieldset>
</div>
<script>
    document.addEventListener( "DOMContentLoaded", () => {
        const avatarSaveButton = document.querySelector( "#avatar-save-button" ) ;
        if( ! avatarSaveButton ) throw "'#avatar-save-button' not found" ;
        avatarSaveButton.addEventListener( 'click', avatarSaveClick ) ;

        for( let nameElement of document.querySelectorAll( ".profile-name b" ) ){
            nameElement.addEventListener( "click", nameClick ) ;
            nameElement.addEventListener( "blur", nameBlur ) ;
            nameElement.addEventListener( "keydown", nameKeydown ) ;
        }
    });
    function avatarSaveClick() {
        const avatarInput = document.querySelector( "#avatar-input" ) ;
        if( ! avatarInput ) throw "'#avatar-input' not found" ;
        if( avatarInput.files.length === 0 ) {
            alert( "select a file" ) ;
            return ;
        }
        let formData = new FormData() ;
        formData.append( "userAvatar", avatarInput.files[0] ) ;
        fetch( "/WebBasics/register/", {
            method: "PUT",
            headers: { },
            body: formData  // наличие файла в formData автоматически сформирует multipart запрос
        }).then( r => r.text() )
            .then( t => {
                console.log(t);
            } ) ;
    }
    function nameKeydown(e) {
        if( e.keyCode === 13 ) {
            e.preventDefault() ;
            e.target.blur() ;  // снять фокус ввода с элемента
            return false ;
        }
    }
    function nameClick(e) {
        e.target.setAttribute( "contenteditable", "true" ) ;
        e.target.focus() ;  // установить фокус ввода на элемент
        e.target.savedText = e.target.innerText ;
    }
    function nameBlur(e) {
        e.target.removeAttribute( "contenteditable" ) ;
        if( e.target.savedText !== e.target.innerText ) {
            if( confirm( "Сохранить изменения?" ) ) {
                const fieldName = e.target.getAttribute( "data-field-name" ) ;
                const url = "/WebBasics/register/?" + fieldName + "=" + e.target.innerText ;
                // console.log( url ) ; return ;
                fetch( url, {
                    method: "PUT",
                    headers: {

                    },
                    body: ""
                }).then( r => r.text() )
                    .then( t => {
                        // OK / error
                        console.log(t)
                        if( t === "OK" ) {
                            location = location ;
                        }
                        else {
                            alert( t ) ;
                            e.target.innerText = e.target.savedText ;
                        }
                    } ) ;
            }
            else {
                e.target.innerText = e.target.savedText ;
            }
        }
    }
</script>
AJAX (Async JavaScript And XML ) - технология асинхронного обмена данными браузера
и сервера.
Суть: браузер формирует запрос (XHR - XMLHttpRequest) и отслеживает его состояния:
 1 - начало отправки данных-запроса
 2 - конец
 3 - начало получения ответа
 4 - конец
с каждым изменением состояния вызывается обработчик события, по 4-му состоянию
ответ готов к анализу.
В современном исполнении создана "оболочка" window.fetch
<pre>
Page (/index.jsp)           --->   GET /index.jsp
HTML                        <---    < !doctype ....
 fetch(/register, PUT)   --------> PUT /register   (resp.getWriter.print("PUT works")
   .then( r <---------------------- body("PUT works")
     => r.text() )
   .then( t =>   // t == "PUT works"

 < form action="/home" method="post"
   submit -------------------------->  POST /home  (resp.send302)
 << unload >> - страница разрушается            |
    302 /home          <------------------------
    ---------------------------------> GET /home
HTML (<< loaded >> )  <------------    < !doctype ....
  < a href="/auth"   ----------------> GET /auth
  << unload >>                             |
HTML (<< loaded >> )  <------------    < !doctype ....
 < form action="/home" method="post"
   submit -------------------------->  POST /home x=10&y=20  (forward->jsp)
 << unload >>                                 |
HTML (<< loaded >> )  <------------    < !doctype ....
  reload(F5)
    --( повторить x=10&y=20 ? )--да--> POST /home x=10&y=20
                         <------нет
</pre>