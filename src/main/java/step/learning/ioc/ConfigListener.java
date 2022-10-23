package step.learning.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;

// Конфигурация иньектора, фильтров и сервлетов
@Singleton
public class ConfigListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ConfigServlet(),
                new ConfigModule()
        );
    }
}
