package database;

public interface AuthorizedDataBase extends DataBase {
    
    String DEFAULT_USERNAME = "root";

    DataBaseAnswer<Void> changeUser(String userName);
}
