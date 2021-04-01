package remoteDataBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandWrapper implements Serializable {
    
    private CommandName name;
    private List<Object> args;

    public CommandWrapper(CommandName name, Object ... args) {
        this.name = name;
        this.args = new ArrayList<Object>(Arrays.asList(args));
    }

    public CommandName getName() {
        return name;
    }

    public List<Object> getArgs() {
        return args;
    }
}
