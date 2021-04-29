package remoteDataBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandWrapper implements Serializable {
    
    private CommandName name;
    private List<Object> args;
    private String userName;
    private String password;

    public CommandWrapper(String userName, String password, CommandName name, Object ... args) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.args = new ArrayList<Object>(Arrays.asList(args));
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public CommandName getName() {
        return name;
    }

    public List<Object> getArgs() {
        return args;
    }
}
