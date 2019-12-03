package data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
    public final static String ID_FIELD_NAME = "login";
    public final static String PASSWORD_FIELD_NAME = "password";
    public final static String NAME_FIELD_NAME = "name";

    @DatabaseField(id = true, canBeNull = false, columnName = ID_FIELD_NAME)
    private String login;

    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;

    @DatabaseField(columnName = PASSWORD_FIELD_NAME)
    private String password;

    public User() {
        //for OrmLite
    }

    public User(String login, String password, String name) {
        this.login = login;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
