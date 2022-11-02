package step.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.UserDAO;
import step.learning.entities.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Singleton
public class CheckMailServlet extends HttpServlet {
    @Inject
    private UserDAO userDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String confirm = req.getParameter("confirm");
        String userId = req.getParameter("userid");
        if (confirm != null) {     // confirm email was clicked
            try {
                User user = userId == null
                        ? (User) req.getAttribute("AuthUser")
                        : userDAO.getUserById(userId);

                if (user == null) // user not found
                    throw new Exception("Invalid user id");

                req.setAttribute("AuthUser", user);

                if (user.getEmailCode() == null) // email already confirmed
                    throw new Exception("Email already confirmed");

                if (!confirm.equals(user.getEmailCode())) { // invalid confirmation code
                    userDAO.isEmailCodeAttempts(user);
                    throw new Exception("Invalid code" + user.getEmailCodeAttempts() + " attempts");
                }

                if (!userDAO.confirmEmail(user)) // error confirming email in database
                    throw new Exception("DB error");

                req.setAttribute("confirm", "OK");  // code confirmed
            } catch (Exception ex) {
                req.setAttribute("confirmError", ex.getMessage());
            }

        }
        req.setAttribute("pageBody", "checkmail.jsp");
        req.getRequestDispatcher("/WEB-INF/_layout.jsp").forward(req, resp);
    }

}

