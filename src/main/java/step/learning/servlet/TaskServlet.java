package step.learning.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.TaskDAO;
import step.learning.entities.Task;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Singleton
public class TaskServlet extends HttpServlet {
    @Inject
    private TaskDAO taskDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Task> tasks = taskDAO.getAllTasks();
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        req.setAttribute("tasks", tasks);

        req.setAttribute("pageBody", "tasks.jsp");
        req.getRequestDispatcher("/WEB-INF/_layout.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        System.out.println("Hello from doPost");
        String taskName = req.getParameter("taskName");
        String taskDescription = req.getParameter("taskDescription");

        String errorMessage = "";
        try {
            if (taskName == null || taskName.isEmpty())
                throw new Exception("Task name is empty");
            if (taskDescription == null || taskDescription.isEmpty())
                throw new Exception("Task description is empty");

            Task task = new Task();
            task.setName(taskName);
            task.setDescription(taskDescription);
            if (taskDAO.addTask(task) == null)
                throw new Exception("DB error");
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }

        if (errorMessage.isEmpty())
            session.setAttribute("message", "Task added");
        else
            session.setAttribute("message", errorMessage);

        resp.sendRedirect(req.getRequestURI());
    }
}
