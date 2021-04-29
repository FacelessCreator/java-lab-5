package database;

public class ProtectedDataManipulator extends DataManipulator {
    
    private ProtectedDataBase dataBase;

    public ProtectedDataManipulator(ProtectedDataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
    }

    public DataBaseAnswer<String> whoami() {
        return dataBase.whoami();
    }

    public DataBaseAnswer<Void> login(String userName, String password) {
        return dataBase.login(userName, password);
    }

    public DataBaseAnswer<Void> register(String userName, String password) {
        return dataBase.register(userName, password);
    }

}
