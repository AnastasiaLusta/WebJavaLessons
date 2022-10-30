package step.learning.dao;

import step.learning.entities.User;
import step.learning.services.DataService;
import step.learning.services.hash.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class UserDAO {
    private final Connection connection ;
    private final HashService hashService ;
    private final DataService dataService ;
    @Inject
    public UserDAO( DataService dataService, HashService hashService ) {
        this.dataService = dataService ;
        this.hashService = hashService ;

        this.connection = dataService.getConnection() ;
    }
    public User getUserById( String userId ) {
        String sql = "SELECT * FROM Users u WHERE u.`id` = ? " ;
        try( PreparedStatement prep =
                     dataService.getConnection().prepareStatement( sql ) ) {
            prep.setString( 1, userId ) ;
            ResultSet res = prep.executeQuery() ;
            if( res.next() ) {
                return new User( res ) ;
            }
        }
        catch( Exception ex ) {
            System.out.println( "UserDAO::getUserById " + ex.getMessage()
                + "\n" + sql + " -- " + userId ) ;
        }
        return null ;
    }

    /**
     * Inserts user in DB `Users` table
     * @param user data to insert
     * @return `id` of new record or null if fails
     */
    public String add( User user ) {
        // генерируем id для новой записи
        String id = UUID.randomUUID().toString() ;
        // генерируем соль
        String salt = hashService.hash( UUID.randomUUID().toString() ) ;
        // генерируем хеш пароля
        String passHash = this.hashPassword( user.getPass(), salt ) ;
        // готовим запрос (подстановка введенных данных!!)
        String sql = "INSERT INTO Users(`id`,`login`,`pass`,`name`,`salt`) VALUES(?,?,?,?,?)" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, id ) ;
            prep.setString( 2, user.getLogin() ) ;
            prep.setString( 3, passHash ) ;
            prep.setString( 4, user.getName() ) ;
            prep.setString( 5, salt ) ;
            prep.executeUpdate() ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            return null ;
        }
        return id ;
    }

    /**
     * Checks User table for login given
     * @param login value to look for
     * @return true if login is in table
     */
    public boolean isLoginUsed( String login ) {
        String sql = "SELECT COUNT(u.`id`) FROM Users u WHERE u.`login`=?" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, login ) ;
            ResultSet res = prep.executeQuery() ;
            res.next() ;
            return res.getInt(1) > 0 ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
            return true ;
        }
    }

    /**
     * Calculates hash (optionally salted) from password
     * @param password Open password string
     * @return hash for DB table
     */
    public String hashPassword( String password, String salt ) {
        return hashService.hash( salt + password + salt ) ;
    }

    /**
     * Gets user form DB by login and password
     * @param login Credentials: login
     * @param pass Credentials: password
     * @return User or null
     */
    public User getUserByCredentials( String login, String pass ) {
        String sql = "SELECT u.* FROM Users u WHERE u.`login`=?" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, login ) ;
            ResultSet res = prep.executeQuery() ;
            if( res.next() ) {
                User user = new User( res ) ;
                // pass - открытый пароль, user.pass - Hash(pass,user.salt)
                String expectedHash = this.hashPassword( pass, user.getSalt() ) ;
                if( expectedHash.equals( user.getPass() ) ) {
                    return user ;
                }
            }
            /*
            Д.З. Определить - если у пользователя нет соли, то проводить
            аутентификацию только по хешу пароля пробные данные: (пароль 123)
            ae5498df-ccbe-4e71-9e5e-73fdd2393dd4,admin,40bd001563085fc35165329ea1ff5c5ecbdbbeef,Administrator,

             */
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
        }
        return null ;
    }
    public User getUserByCredentialsOld( String login, String pass ) {
        String sql = "SELECT u.* FROM Users u WHERE u.`login`=? AND u.`pass`=?" ;
        try( PreparedStatement prep = connection.prepareStatement( sql ) ) {
            prep.setString( 1, login ) ;
            prep.setString( 2, this.hashPassword( pass, "" ) ) ;
            ResultSet res = prep.executeQuery() ;
            if( res.next() ) return new User( res ) ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            System.out.println( sql ) ;
        }
        return null ;
    }

    public boolean updateUser( User user ) {
        if( user == null || user.getId() == null ) return false ;

        // Задание: сформировать запрос, учитывая только те данные, которые не null (в user)
        Map<String, String> data = new HashMap<>() ;
        if( user.getName() != null ) data.put( "name", user.getName() ) ;
        if( user.getLogin() != null ) data.put( "login", user.getLogin() ) ;
        if( user.getAvatar() != null ) data.put( "avatar", user.getAvatar() ) ;
        String sql = "UPDATE Users u SET " ;
        boolean needComma = false ;
        for( String fieldName : data.keySet() ) {
            sql += String.format( "%c u.`%s` = ?", ( needComma ? ',' : ' ' ), fieldName ) ;
            needComma = true ;
        }
        sql += " WHERE u.`id` = ? " ;
        if( ! needComma ) {  // не было ни одного параметра
            return false ;
        }
        try( PreparedStatement prep = dataService.getConnection().prepareStatement( sql ) ) {
            int n = 1;
            for( String fieldName : data.keySet() ) {
                prep.setString( n, data.get( fieldName ) ) ;
                ++n ;
            }
            prep.setString( n, user.getId() ) ;
            prep.executeUpdate() ;
        }
        catch( SQLException ex ) {
            System.out.println( "UserDAO::updateUser" + ex.getMessage() ) ;
            return false ;
        }
        return true ;
    }
}
/*
Аутентификация с "солью"
Соль (крипто-соль) - данные (обычно случайные), добавляемые перед
хешированиям к другим данным для обеспечения отличия хешей для
одинаковых исходных данных (паролей).

 */