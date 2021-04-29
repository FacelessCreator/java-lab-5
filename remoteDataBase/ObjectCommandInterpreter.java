package remoteDataBase;

import java.util.List;

import SQLDataBase.UserAgent;
import database.AuthorizedDataBase;
import database.DataBaseAnswer;
import movie.*;

public class ObjectCommandInterpreter {
    
    private AuthorizedDataBase dataBase;

    private UserAgent userAgent;

    public ObjectCommandInterpreter(AuthorizedDataBase dataBase, UserAgent userAgent) {
        this.dataBase = dataBase;
        this.userAgent = userAgent;
    }

    public Object interpret(CommandWrapper command) {
        String userName = command.getUserName();
        String password = command.getPassword();
        boolean userAccepted = userAgent.checkUser(userName, password).code == DataBaseAnswer.CODE_SUCCESS;
        CommandName commandName = command.getName();
        List<Object> args = command.getArgs();
        try {
            Object result;
            if (userAccepted) {
                dataBase.changeUser(userName);
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
                    case PING:
                        result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_SUCCESS, null);
                        break;
                    default:
                        result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_REQUEST, null);
                        break; 
                }
            } else {
                switch (commandName) {
                    case REGISTER:
                        DataBaseAnswer<Void> agentResult = userAgent.registerUser(userName, password);
                        if (agentResult.code == DataBaseAnswer.CODE_SUCCESS) {
                            result = dataBase.changeUser(userName);
                        } else {
                            result = agentResult;
                        }
                        break;
                    default:
                        result = new DataBaseAnswer<Void>(DataBaseAnswer.CODE_PERMISSION_DENIED, null);
                        break;
                }
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
