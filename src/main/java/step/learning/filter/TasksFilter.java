package step.learning.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.TaskDAO;
import step.learning.entities.Task;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class TasksFilter implements Filter {
    FilterConfig filterConfig;
    private final TaskDAO taskDAO;

    @Inject
    public TasksFilter(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws javax.servlet.ServletException, java.io.IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // set changes to database
        if (request.getMethod().equalsIgnoreCase("PUT")) {
            String taskId = request.getParameter("taskId");
            String taskName = request.getParameter("taskName");
            String taskDescription = request.getParameter("taskDescription");

            Task changed = taskDAO.getTaskById(taskId);
            changed.setName(taskName);
            changed.setDescription(taskDescription);
            taskDAO.updateTask(changed);
            short taskIsDone = Short.parseShort(request.getParameter("taskIsDone"));
            if (taskIsDone == 1) {
                taskDAO.markTaskAsNotDone(taskId);
            } else {
                taskDAO.markTaskAsDone(taskId);
            }
        }

        if (request.getMethod().equalsIgnoreCase("DELETE")) {
            String taskId = request.getParameter("taskId");
            taskDAO.deleteTask(taskId);
        }

        filterChain.doFilter(request, response);

    }

    public void destroy() {
        this.filterConfig = null;
    }
}
