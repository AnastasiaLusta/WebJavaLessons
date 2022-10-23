package step.learning.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.DataService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Singleton
public class AuthFilter  implements Filter {
    private FilterConfig filterConfig ;
    private final UserDAO userDAO ;

    @Inject
    public AuthFilter( UserDAO userDAO ) {
        this.userDAO = userDAO ;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig ;
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest ;
        HttpServletResponse response = (HttpServletResponse) servletResponse ;
        HttpSession session = request.getSession() ;
        // Традиционно, сначала проверяется log out
        if( request.getParameter( "logout" ) != null ) {
            session.removeAttribute( "AuthUserId" ) ;
            // После выхода переадресация - на главную страницу
            response.sendRedirect( request.getContextPath() ) ;
            return ;
        }
        // затем log in
        if( request.getMethod().equalsIgnoreCase( "POST" ) ) {
            if( "auth-form".equals( request.getParameter( "form-id" ) ) ) {
                String userLogin    = request.getParameter( "userLogin" ) ;
                String userPassword = request.getParameter( "userPassword" ) ;
                User user = userDAO.getUserByCredentials( userLogin, userPassword ) ;
                if( user == null ) {
                    session.setAttribute( "AuthError", "Credentials incorrect" ) ;
                }
                else {
                    session.setAttribute( "AuthUserId", user.getId() ) ;
                }
                System.out.println( userLogin + "  " + userPassword + " " +
                        ( user == null ? "null" : user.getId() ) ) ;
                response.sendRedirect( request.getRequestURI() ) ;
                return ;
            }
        }

        String authData = (String) session.getAttribute( "AuthError" ) ;
        if( authData != null ) {  // В сессии хранится ошибка авторизации
            request.setAttribute( "AuthError", authData ) ;
            session.removeAttribute( "AuthError" ) ;
        }
        authData = (String) session.getAttribute( "AuthUserId" ) ;
        if( authData != null ) {  // В сессии - Id авторизованного пользователя
            request.setAttribute( "AuthUser", userDAO.getUserById( authData ) ) ;
        }

        filterChain.doFilter( servletRequest, servletResponse ) ;
    }

    public void destroy() {
        this.filterConfig = null ;
    }
}
/*
Д.З. Реализовать работу "кнопки" Log out - выход из авторизованного режима
 */