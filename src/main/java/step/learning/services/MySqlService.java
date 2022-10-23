package step.learning.services;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlService implements DataService {
    private final String connectionString = "jdbc:mysql://localhost:3306/java191" +
            "?useUnicode=true&characterEncoding=UTF-8" ;
    private final String dbUser = "user191" ;
    private final String dbPass = "pass191" ;
    private Connection connection ;

    public Connection getConnection() {
        if( connection == null ) {
            try {
                Class.forName( "com.mysql.cj.jdbc.Driver" ) ;
                connection = DriverManager.getConnection(
                        connectionString, dbUser, dbPass ) ;
            }
            catch( Exception ex ) {
                System.out.println( "MysqlDataService::getConnection() -- " +
                        ex.getMessage() ) ;
            }
        }
        return connection ;
    }
}
