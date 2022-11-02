package step.learning.dao;

import step.learning.entities.User;
import step.learning.services.DataService;
import step.learning.services.EmailService;
import step.learning.services.hash.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Singleton
public class UserDAO {
    private final Connection connection;
    private final HashService hashService;
    private final EmailService emailService;

    @Inject
    public UserDAO(DataService dataService, HashService hashService, EmailService emailService) {
        this.hashService = hashService;
        this.emailService = emailService;
        this.connection = dataService.getConnection();
    }

    public boolean confirmEmail(User user) {
        if (user.getId() == null) return false;
        String sql = "UPDATE Users SET email_code = NULL WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Error while confirming email: " + sql);
            return false;
        }
        return true;
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM Users u WHERE u.`id` = ? ";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                return new User(res);
            }
        } catch (Exception ex) {
            System.out.println("UserDAO::getUserById " + ex.getMessage() + "\n" + sql + " -- " + userId);
        }
        return null;
    }

    /**
     * Inserts user in DB `Users` table
     *
     * @param user data to insert
     * @return `id` of new record or null if fails
     */
    public String add(User user) {
        // генерируем id для новой записи
        String id = UUID.randomUUID().toString();
        // генерируем соль
        String salt = hashService.hash(UUID.randomUUID().toString());
        // генерируем хеш пароля
        String passHash = this.hashPassword(user.getPass(), salt);
        // готовим запрос (подстановка введенных данных!!)

        user.setEmailCode(UUID.randomUUID().toString().substring(0, 6)); // generate email confirmation code

        String sql = "INSERT INTO Users(`id`,`login`,`pass`,`name`,`salt`,`avatar`, `email`, `email_code`) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            prep.setString(2, user.getLogin());
            prep.setString(3, passHash);
            prep.setString(4, user.getName());
            prep.setString(5, salt);
            prep.setString(6, user.getAvatar());
            prep.setString(7, user.getEmail());
            prep.setString(8, user.getEmailCode());
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        String text = String.format("Hello! Your code is %s", user.getEmailCode());
        emailService.send(user.getEmail(), "Email confirmation", text); // send email confirmation code

        return id;
    }

    /**
     * Checks User table for login given
     *
     * @param login value to look for
     * @return true if login is in table
     */
    public boolean isLoginUsed(String login) {
        String sql = "SELECT COUNT(u.`id`) FROM Users u WHERE u.`login`=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            res.next();
            return res.getInt(1) > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
            return true;
        }
    }

    /**
     * Calculates hash (optionally salted) from password
     *
     * @param password Open password string
     * @return hash for DB table
     */
    public String hashPassword(String password, String salt) {
        return hashService.hash(salt + password + salt);
    }

    /**
     * Gets user form DB by login and password
     *
     * @param login Credentials: login
     * @param pass  Credentials: password
     * @return User or null
     */
    public User getUserByCredentials(String login, String pass) {
        String sql = "SELECT u.* FROM Users u WHERE u.`login`=?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet res = prep.executeQuery();
            if (res.next()) {
                User user = new User(res);
                // pass - открытый пароль, user.pass - Hash(pass,user.salt)
                String expectedHash = this.hashPassword(pass, user.getSalt());
                if (expectedHash.equals(user.getPass())) {
                    return user;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) return false;

        // Задание: сформировать запрос, учитывая только те данные, которые не null (в user)
        Map<String, String> data = new HashMap<>();
        Map<String, Integer> dataNumeric = new HashMap<>();
        if (user.getName() != null) data.put("name", user.getName());
        if (user.getLogin() != null) data.put("login", user.getLogin());
        if (user.getAvatar() != null) data.put("avatar", user.getAvatar());
        if (user.getEmail() != null) {
            user.setEmailCode(UUID.randomUUID().toString().substring(0, 6));
            data.put("email", user.getEmail());
            data.put("email_code", user.getEmailCode());
            dataNumeric.put("email_code_attempts", 0);
        }

        if (user.getPass()!= null){
            String salt = hashService.hash(UUID.randomUUID().toString());
            String passHash = this.hashPassword(user.getPass(), salt);
            data.put("pass", passHash);
            data.put("salt", salt);
        }
        StringBuilder sql = new StringBuilder("UPDATE Users u SET ");
        boolean needComma = false;
        for (String fieldName : data.keySet()) {
            sql.append(String.format("%c u.`%s` = ?", (needComma ? ',' : ' '), fieldName));
            needComma = true;
        }
        for (String fieldName : dataNumeric.keySet()) {
            sql.append(String.format("%c u.`%s` = %d", (needComma ? ',' : ' '), fieldName, dataNumeric.get(fieldName)));
            needComma = true;
        }
        sql.append(" WHERE u.`id` = ? ");
        if (!needComma) {  // не было ни одного параметра
            return false;
        }
        try (PreparedStatement prep = connection.prepareStatement(sql.toString())) {
            int n = 1;
            for (String fieldName : data.keySet()) {
                prep.setString(n, data.get(fieldName));
                ++n;
            }
            prep.setString(n, user.getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("UserDAO::updateUser" + ex.getMessage());
            return false;
        }

        if (user.getEmailCode() != null) {
            String text = String.format("Hello! Your code is %s", user.getEmailCode());
            emailService.send(user.getEmail(), "Email confirmation", text);
        }
        return true;
    }

    public boolean isEmailCodeAttempts(User user){
        if (user == null || user.getId() == null) return false;
        String sql = "UPDATE Users u SET u.`email_code_attempts` = u.`email_code_attempts` + 1 WHERE u.`id` = ?";
        try (PreparedStatement prep = connection.prepareStatement(sql)) {
            prep.setString(1, user.getId());
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("UserDAO::isEmailCodeAttempts"+sql);
        }
        user.setEmailCodeAttempts(user.getEmailCodeAttempts()+1);
        return true;
    }
}
/*
Аутентификация с "солью"
Соль (крипто-соль) - данные (обычно случайные), добавляемые перед
хешированиям к другим данным для обеспечения отличия хешей для
одинаковых исходных данных (паролей).

 */