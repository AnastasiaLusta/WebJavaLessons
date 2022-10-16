package step.learning;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/servlet")
public class ViewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        var session = request.getSession();
        var userInput = (String) session.getAttribute("userInput");
        request.setAttribute("userInput", userInput);
        if (userInput != null) {
            session.removeAttribute("userInput");
        }
        request.getRequestDispatcher("WEB-INF/servlet.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String userInput = req.getParameter("userInput");  // form-data
        req.getSession().setAttribute("userInput", userInput);
        resp.sendRedirect(req.getRequestURI());
    }
}