package SQLDataBase;

import java.sql.Connection;
import java.sql.Statement;

import database.DataBaseAnswer;
import etc.Hashes;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAgent {
    
    private String dataBaseURL;
    private String dataBaseUserName; // user name for SQL DB
    private String dataBasePassword;

    private Connection dataBaseConnection;
    private Statement dataBaseStatement;

    private static final String USERS_TABLE_NAME = "s312783_users";
    private static final String USERS_SEQUENCE_NAME = "s312783_sequence_users";
    
    public UserAgent(String dataBaseURL, String userName, String password) {
        this.dataBaseURL = dataBaseURL;
        this.dataBaseUserName = userName;
        this.dataBasePassword = password;

        try {
            dataBaseConnection = DriverManager.getConnection(dataBaseURL, userName, password);
            dataBaseStatement = dataBaseConnection.createStatement();
        } catch (SQLException e) {
            //TODO: handle exception
        }
    }

    public DataBaseAnswer<Void> checkUser(String userName, String password) {
        String query = "select count(id) from %s where name='%s' and password='%s'";
        query = String.format(query, USERS_TABLE_NAME, userName, Hashes.getSHA384(password));
        try {
            ResultSet resultSet = dataBaseStatement.executeQuery(query);
            resultSet.next();
            if (resultSet.getInt(1) > 0) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
            } else {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_AUTHORIZING_DENIED, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
    }

    public DataBaseAnswer<Void> registerUser(String userName, String password) {
        String query = "insert into %s values (nextval('%s'), '%s', '%s')";
        query = String.format(query, USERS_TABLE_NAME, USERS_SEQUENCE_NAME, userName, Hashes.getSHA384(password));
        try {
            int result = dataBaseStatement.executeUpdate(query);
            if (result != 0) {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
            } else {
                return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_AUTHORIZING_DENIED, null);
            }
        } catch (SQLException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SQL_ERROR, null);
        }
    }

}
