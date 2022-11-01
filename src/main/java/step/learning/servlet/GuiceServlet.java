package step.learning.servlet;

import step.learning.services.hash.MD5HashService;
import step.learning.services.hash.Sha1HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class GuiceServlet extends HttpServlet {

    private final MD5HashService md5;
    private final Sha1HashService sha1;

    @Inject
    public GuiceServlet(MD5HashService md5, Sha1HashService sha1) {
        this.md5 = md5;
        this.sha1 = sha1;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/guice.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String toCode = req.getParameter("toHash");

        req.getSession().setAttribute("hashed1", "MD5: " + md5.hash(toCode));
        req.getSession().setAttribute("hashed2", "SHA1: " + sha1.hash(toCode));

        resp.sendRedirect(req.getRequestURI());
    }
}