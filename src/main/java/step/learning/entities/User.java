package step.learning.entities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String id;
    private String login;
    private String pass;
    private String name;
    private String salt;
    private String avatar;

    private String email;

    private String emailCode;
    
    private int emailCodeAttempts;

    public User() {

    }

    public User(ResultSet res) throws SQLException {
        id = res.getString("id");
        login = res.getString("login");
        pass = res.getString("pass");
        name = res.getString("name");
        salt = res.getString("salt");
        avatar = res.getString("avatar");
        email = res.getString("email");
        emailCode = res.getString("email_code");
        emailCodeAttempts = res.getInt("email_code_attempts");
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public int getEmailCodeAttempts() {
        return emailCodeAttempts;
    }

    public void setEmailCodeAttempts(int emailCodeAttempts) {
        this.emailCodeAttempts = emailCodeAttempts;
    }
}
