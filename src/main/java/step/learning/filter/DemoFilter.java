package step.learning.filter;

import com.google.inject.Singleton;

import javax.servlet.*;

import java.io.IOException;

@Singleton
public class DemoFilter implements Filter {
    FilterConfig filterConfig ;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig ;
    }

    public void doFilter(                    // Основной метод фильтра
            ServletRequest servletRequest,   // запрос - (не НТТР)
            ServletResponse servletResponse, // ответ  - (не НТТР)
            FilterChain filterChain)         // Ссылка на "следующий" фильтр
            throws IOException, ServletException {

        servletRequest.setAttribute( "DemoFilter", "Filter Works!" ) ;
        filterChain.doFilter( servletRequest, servletResponse ) ;
    }

    public void destroy() {
        this.filterConfig = null ;
    }
}
