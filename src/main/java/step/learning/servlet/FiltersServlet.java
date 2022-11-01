package step.learning.servlet;

import com.google.inject.Inject;
import step.learning.services.DataService;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class FiltersServlet extends HttpServlet {

    private final DataService dataService;

    @Inject
    public FiltersServlet(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql = "SELECT COUNT(*) FROM Users"; // sql query to get the number of users
//        DataService db = (DataService) req.getAttribute("DataService"); // get the DataService from the request

        String result = "No users"; // default result

        try (Statement statement = dataService.getConnection().createStatement()) {
            ResultSet res = statement.executeQuery(sql);
            if (res.next()) {
                result = "" + res.getInt(1); // get the first column
            }
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
        }

        sql = "SELECT * FROM Users"; // sql query to get the users
        List<String> users = new ArrayList<>(); // list of users

        try (Statement statement = dataService.getConnection().createStatement()) {
            ResultSet res = statement.executeQuery(sql);
            while (res.next()) {
                users.add(res.getString("name") + " " + res.getString("login")); // get the name and login
            }
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
        }

        req.setAttribute("Count", result); // set the result to the request
        req.setAttribute("Users", users.toArray(new String[0])); // set the users to the request

        req.getRequestDispatcher("WEB-INF/filters.jsp").forward(req, resp);
    }
}