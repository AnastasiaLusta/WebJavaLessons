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
        filter("/*").through(TasksFilter.class);

        // ...  и сервлетов
        serve( "/filters" ).with( FiltersServlet.class ) ;
        serve( "/servlet" ).with( ViewServlet.class ) ;
        serve( "/register/" ).with( RegisterServlet.class ) ;
        serve( "/image/*" ).with( DownloadServlet.class ) ;
        serve( "/profile" ).with( ProfileServlet.class ) ;
        serve( "/checkmail/" ).with( CheckMailServlet.class ) ;
        serve("/tasks").with(TaskServlet.class);
        serve( "/" ).with( HomeServlet.class ) ;
    }
}
