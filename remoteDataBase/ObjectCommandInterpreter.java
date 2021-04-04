package remoteDataBase;

import java.util.List;

import database.DataBase;
import database.DataBaseAnswer;
import movie.*;

public class ObjectCommandInterpreter {
    
    private DataBase dataBase;

    public ObjectCommandInterpreter(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public Object interpret(CommandWrapper command) {
        CommandName commandName = command.getName();
        List<Object> args = command.getArgs();
        try {
            Object result;
            switch (commandName) {
                case ABOUT:
                    result = dataBase.about();
                    break;
                case GET_STATUS:
                    result = dataBase.getStatus();
                    break;
                case LENGTH:
                    result = dataBase.length();
                    break;
                case ADD:
                    result = dataBase.add((Movie) args.get(0));
                    break;
                case REMOVE:
                    result = dataBase.remove((Long) args.get(0));
                    break;
                case GET:
                    result = dataBase.get((Long) args.get(0));
                    break;
                case REPLACE:
                    result = dataBase.replace((Long) args.get(0), (Movie) args.get(1));
                    break;
                case CLEAR:
                    result = dataBase.clear();
                    break;
                case GET_ID_LIST:
                    result = dataBase.getIdList();
                    break;
                case GET_MAX_ELEMENT:
                    result = dataBase.getMaxElement();
                    break;
                case GET_MIN_ELEMENT:
                    result = dataBase.getMinElement();
                    break;
                case SEARCH_BY_OPERATOR:
                    result = dataBase.searchByOperator((Person) args.get(0));
                    break;
                case LOAD:
                    result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_PERMISSION_DENIED, null);
                    //result = dataBase.load();
                    break;
                case SAVE:
                    result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_PERMISSION_DENIED, null);
                    //result = dataBase.save();
                    break;
                default:
                    result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_REQUEST, null);
                    break; 
            }
            return result;
        } catch (Exception e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_REQUEST, null);
        }
    }

    public Object interpret(Object command) {
        try {
            CommandWrapper wrapper = (CommandWrapper) command;
            return interpret(wrapper);
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_REQUEST, null);
        }
    } 
}
