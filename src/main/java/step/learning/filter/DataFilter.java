package step.learning.filter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.DataService;

import javax.servlet.*;
import java.io.IOException;

@Singleton
public class DataFilter implements Filter {
    private final DataService dataService;
    FilterConfig filterConfig;

    @Inject
    public DataFilter(DataService dataService) {
        this.dataService = dataService;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (dataService.getConnection() == null) {
            servletRequest.getRequestDispatcher("WEB-INF/static.jsp")
                    .forward(servletRequest, servletResponse);
        } else {
            servletRequest.setAttribute("DataService", dataService);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
    // Реализовать фильтр подключения к БД
    // Обеспечить переход на статическую страницу если нет подключения
    // На стартовой странице вывести данные о кол-ве записей в БД
    //  (любая таблица для примера). * - вывести данные из таблицы.

    public void destroy() {
        this.filterConfig = null;
    }
}
