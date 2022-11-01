package step.learning.filter;

import com.google.inject.Singleton;

import javax.servlet.*;
import java.io.IOException;

@Singleton
public class CharsetFilter  implements Filter {
    FilterConfig filterConfig ;

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig ;
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding( "UTF-8" ) ;
        servletResponse.setCharacterEncoding( "UTF-8" ) ;
        filterChain.doFilter( servletRequest, servletResponse ) ;
    }

    public void destroy() {
        this.filterConfig = null ;
    }
}
