package step.learning;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(); // gets session
        String loginInput = (String)req.getSession().getAttribute("loginInput"); // gets the loginInput from the session
        String errorMessage = (String)req.getSession().getAttribute("errorMessage"); // gets the errorMessage from the session
        req.setAttribute("loginInput", loginInput); // sets the loginInput to the request
        if (loginInput != null) {
            session.removeAttribute("loginInput"); // removes the loginInput from the session if it exists
        }
        req.getRequestDispatcher("WEB-INF/register.jsp").forward(req, resp); // forwards the request to the register.jsp
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String loginInput = req.getParameter("loginInput"); // gets the value of the input with the name "loginInput"
        String passwordInput = req.getParameter("passwordInput"); // gets the value of the input with the name "passwordInput"
        String passwordInputRep = req.getParameter("passwordInputRep"); // gets the value of the input with the name "passwordInputRep"
        if (!(loginInput.isEmpty()) && !(passwordInput.isEmpty())){
            if (passwordInput.equals(passwordInputRep)){
                req.getSession().setAttribute("loginInput", loginInput); // sets the loginInput to the session
                resp.sendRedirect(req.getRequestURI()); // redirects to the register page
            } else {
               resp.sendRedirect(req.getRequestURI()); // redirects to the register page
            }
        } else {
           resp.sendRedirect(req.getRequestURI()); // redirects to the register page
        }
    }
}
