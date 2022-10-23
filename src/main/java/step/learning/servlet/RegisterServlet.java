package step.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDAO;
import step.learning.entities.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Singleton
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO ;

    @Inject
    public RegisterServlet( UserDAO userDAO ) {
        this.userDAO = userDAO ;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // check if there are saved in the session data from the previous processing
        var session = req.getSession() ; // get session
        var regError = (String) session.getAttribute( "regError" ) ; // get error message
        var regOk = (String) session.getAttribute( "regOk" ) ; // get ok message
        if( regError != null ) {  // if error message exists
            req.setAttribute( "regError", regError ) ;
            session.removeAttribute( "regError" ) ;  // delete error message from session
        }
        if( regOk != null ) {  // if ok message exists
            req.setAttribute( "regOk", regOk ) ;
            session.removeAttribute( "regOk" ) ;  // delete ok message from session
        }

        req.setAttribute( "pageBody", "register.jsp" ) ;
        req.getRequestDispatcher( "/WEB-INF/_layout.jsp" )
                .forward( req, resp ) ;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession() ;

        // get data from registration form
        String userLogin = req.getParameter( "userLogin" ) ; // get login
        String userPassword = req.getParameter( "userPassword" ) ; // get password
        String confirmPassword = req.getParameter( "confirmPassword" ) ; // get password confirmation
        String userName = req.getParameter( "userName" ) ; // get user name

        // validate data
        String errorMessage = null ;
        try {
            if( userLogin == null || userLogin.isEmpty() ) {
                throw new Exception( "Login could not be empty" ) ;
            }
            if( ! userLogin.equals( userLogin.trim() ) ) {
                throw new Exception( "Login could not contain trailing spaces" ) ;
            }
            if( userDAO.isLoginUsed( userLogin ) ) {
                throw new Exception( "Login is already in use" ) ;
            }
            if( userPassword == null || userPassword.isEmpty() ) {
                throw new Exception( "Password could not be empty" ) ;
            }
            if( ! userPassword.equals( confirmPassword ) ) {
                throw new Exception( "Passwords mismatch" ) ;
            }
            if( userName == null || userName.isEmpty() ) {
                throw new Exception( "Name could not be empty" ) ;
            }
            if( ! userName.equals( userName.trim() ) ) {
                throw new Exception( "Name could not contain trailing spaces" ) ;
            }
            var user = new User() ;
            user.setName( userName ) ;
            user.setLogin( userLogin ) ;
            user.setPass( userPassword ) ;
            if( userDAO.add( user ) == null ) {
                throw new Exception( "Server error, try later" ) ;
            }
        }
        catch( Exception ex ) {
            errorMessage = ex.getMessage() ;
        }
        // check if there are errors
        if( errorMessage != null ) {  // if error
            session.setAttribute( "regError", errorMessage ) ;
        }
        else {  // if no errors
            session.setAttribute( "regOk", "Registration successful" ) ;
        }
        resp.sendRedirect( req.getRequestURI() ) ;
    }
}
