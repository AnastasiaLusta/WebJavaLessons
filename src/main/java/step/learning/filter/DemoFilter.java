package step.learning.filter;

import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Singleton
public class DemoFilter implements Filter {
    private FilterConfig filterConfig ;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig ;
    }

    public void doFilter(                    // Основной метод фильтра
            ServletRequest servletRequest,   // запрос - (не НТТР)
            ServletResponse servletResponse, // ответ  - (не НТТР)
            FilterChain filterChain)         // Ссылка на "следующий" фильтр
            throws IOException, ServletException {

        /* // "защита" от того, что привязка "/" сработает как "/*" и будет принимать все запросы
        HttpServletRequest request = (HttpServletRequest) servletRequest ;
        if( request.getServletPath().equals( "/" ) ) {
            request.getRequestDispatcher( "home" ).forward( request, servletResponse ) ;
            return ;
        }*/
        // System.out.println( "Filter starts" ) ;
        servletRequest.setAttribute( "DemoFilter", "Filter Works!" ) ;
        filterChain.doFilter( servletRequest, servletResponse ) ;
        // System.out.println( "Filter ends" ) ;
    }

    public void destroy() {
        this.filterConfig = null ;
    }
}
