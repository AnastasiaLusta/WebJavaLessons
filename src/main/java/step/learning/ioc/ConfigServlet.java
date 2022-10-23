package step.learning.ioc;

import com.google.inject.servlet.ServletModule;
import step.learning.filter.*;
import step.learning.servlet.*;

public class ConfigServlet extends ServletModule {
    @Override
    protected void configureServlets() {
        // Программная замена web.xml - конфигурация фильтров ...
        filter( "/*" ).through( CharsetFilter.class ) ;
        filter( "/*" ).through( DataFilter.class ) ;
        filter( "/*" ).through( AuthFilter.class ) ;
        filter( "/*" ).through( DemoFilter.class ) ;

        // ...  и сервлетов
        serve( "/filters" ).with( FiltersServlet.class ) ;
        serve( "/servlet" ).with( ViewServlet.class ) ;
        serve( "/register/" ).with( RegisterServlet.class ) ;
        serve("/guice").with(GuiceServlet.class);
        serve( "/" ).with( HomeServlet.class ) ;
    }
}
