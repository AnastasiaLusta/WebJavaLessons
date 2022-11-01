package step.learning.ioc;

import com.google.inject.AbstractModule;
import step.learning.services.*;
import step.learning.services.hash.*;

public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        // Конфигурация служб-поставщиков
        bind( DataService.class ).to( MySqlService.class ) ;
        bind( HashService.class ).to( Sha1HashService.class ) ;
        bind( EmailService.class ).to( GmailService.class ) ;
    }
}
