package database;

public interface ProtectedDataBase extends DataBase {
    
    String DEFAULT_USERNAME = "root";
    String DEFAULT_PASSWORD = "none";

    DataBaseAnswer<String> whoami();

    DataBaseAnswer<Void> login(String userName, String password);

    DataBaseAnswer<Void> register(String userName, String password);
}
