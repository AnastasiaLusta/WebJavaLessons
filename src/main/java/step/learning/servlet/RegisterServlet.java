package step.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.services.MimeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@WebServlet("/register/")
@MultipartConfig
@Singleton
public class RegisterServlet extends HttpServlet {
    @Inject private UserDAO userDAO ;
    @Inject private MimeService mimeService ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // check if there are saved in the session data from the previous processing
        HttpSession session = req.getSession() ; // get session
        String regError = (String) session.getAttribute( "regError" ) ; // get error message
        String regOk = (String) session.getAttribute( "regOk" ) ; // get ok message
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
        String userName = req.getParameter( "userName" ) ; // get username
        Part userAvatar = req.getPart( "userAvatar" ) ; // get avatar

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

            if( userAvatar == null ) {  // it is possible that the user has not selected an avatar
                throw new Exception( "Form integrity violation" ) ;
            }
            long size = userAvatar.getSize() ;
            String savedName = null ;
            if( size > 0 ) {  // check if the user has selected an avatar by checking the size of the file
                // process avatar
                String userFilename = userAvatar.getSubmittedFileName() ;  // имя приложенного файла
                // divide file name into name and extension
                int dotPosition = userFilename.lastIndexOf( '.' ) ;
                if( dotPosition == -1 ) {
                    throw new Exception( "File extension required" ) ;
                }
                String extension = userFilename.substring( dotPosition ) ;
                if( ! mimeService.isImage( extension ) ) {
                    throw new Exception( "File type unsupported" ) ;
                }
                savedName = UUID.randomUUID() + extension ;
                // saving avatar
                String path = req.getServletContext().getRealPath( "/" ) ;  // ....\target\WebBasics\
                File file = new File( path + "../upload/" + savedName ) ;
                Files.copy( userAvatar.getInputStream(), file.toPath() ) ;
            }

            User user = new User() ;
            user.setName( userName ) ;
            user.setLogin( userLogin ) ;
            user.setPass( userPassword ) ;
            user.setAvatar( savedName ) ;
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
            session.setAttribute("savedLogin", userLogin); // saves login if error
            session.setAttribute("savedName", userName); // saves name if error
        }
        else {  // if no errors
            session.setAttribute( "regOk", "Registration successful" ) ;
        }
        resp.sendRedirect( req.getRequestURI() ) ;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User changes = new User() ;
        User authUser = (User) req.getAttribute( "AuthUser" ) ;
        Part userAvatar = null ;
        try {
            userAvatar = req.getPart( "userAvatar" ) ;
        } catch( Exception ignored ) { }
/*
Д.З. Реализовать загрузку файла-аватарки, заменить у пользователя данные
! не забыть удалить старый файл
 */
        if( userAvatar != null ) {
            resp.getWriter().print( "File '" + userAvatar.getSubmittedFileName() + "' in use" ) ;
            return ;
        }
        String reply ;
        String login = req.getParameter( "login" ) ;
        if( login != null ) {
            if( userDAO.isLoginUsed( login ) ) {
                resp.getWriter().print( "Login '" + login + "' in use" ) ;
                return ;
            }
            changes.setLogin( login ) ;
        }
        changes.setId( authUser.getId() ) ;
        changes.setName( req.getParameter( "name" ) ) ;
        reply =
                userDAO.updateUser( changes )
                        ? "OK"
                        : "Update error" ;
        resp.getWriter().print( reply ) ;
    }

}
