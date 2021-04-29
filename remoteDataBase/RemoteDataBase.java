package remoteDataBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import database.ProtectedDataBase;
import database.DataBase;
import database.DataBaseAnswer;
import etc.Serializing;
import movie.*;

public class RemoteDataBase implements ProtectedDataBase {

    private String host;
    private int port;

    private String userName = ProtectedDataBase.DEFAULT_USERNAME;
    private String password = ProtectedDataBase.DEFAULT_PASSWORD;

    public RemoteDataBase(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Object request(String host, int port, CommandWrapper command) {
        try {
            Socket socket = new Socket(host, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            DataOutputStream out = new DataOutputStream(outputStream);
            byte[] objData = Serializing.serializeObject(command);
            out.writeInt(objData.length);
            outputStream.write(objData);
            outputStream.flush();

            DataInputStream in = new DataInputStream(inputStream);
            int answerLength = in.readInt();
            byte[] answerData = new byte[answerLength];
            in.readFully(answerData);
            Object answerObject = null;
            try {
                answerObject = Serializing.deserializeObject(answerData);
            } catch (ClassNotFoundException e) {
                return new DataBaseAnswer<String>(DataBaseAnswer.CODE_BAD_ANSWER, null);
            }
            socket.close();
            return answerObject;
        } catch (IOException e) {
            //System.err.println(e);
            return new DataBaseAnswer<String>(DataBaseAnswer.CODE_CONNECTION_FAILED, null);
        }
    }

    @Override
    public DataBaseAnswer<String> whoami() {
        return new DataBaseAnswer<String>(DataBaseAnswer.CODE_SUCCESS, userName);
    }

    @Override
    public DataBaseAnswer<Void> register(String userName, String password) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.REGISTER);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            if (res.code == DataBaseAnswer.CODE_SUCCESS) {
                this.userName = userName;
                this.password = password;
            }
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    @Override
    public DataBaseAnswer<Void> login(String userName, String password) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.PING);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            if (res.code == DataBaseAnswer.CODE_SUCCESS) {
                this.userName = userName;
                this.password = password;
            }
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<String> about() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.ABOUT);
        try {
            DataBaseAnswer<String> res = (DataBaseAnswer<String>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<String>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<String> getStatus() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.GET_STATUS);
        try {
            DataBaseAnswer<String> res = (DataBaseAnswer<String>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<String>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Integer> length() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.LENGTH);
        try {
            DataBaseAnswer<Integer> res = (DataBaseAnswer<Integer>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Integer>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Long> add(Movie movie) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.ADD, movie);
        try {
            DataBaseAnswer<Long> res = (DataBaseAnswer<Long>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Long>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Void> remove(long id) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.REMOVE, id);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Movie> get(long id) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.GET, id);
        try {
            DataBaseAnswer<Movie> res = (DataBaseAnswer<Movie>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Void> replace(long id, Movie movie) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.REPLACE, id, movie);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Void> clear() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.CLEAR);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<List<Long>> getIdList() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.GET_ID_LIST);
        try {
            DataBaseAnswer<List<Long>> res = (DataBaseAnswer<List<Long>>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<List<Long>>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Movie> getMaxElement() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.GET_MAX_ELEMENT);
        try {
            DataBaseAnswer<Movie> res = (DataBaseAnswer<Movie>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Movie> getMinElement() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.GET_MIN_ELEMENT);
        try {
            DataBaseAnswer<Movie> res = (DataBaseAnswer<Movie>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Movie>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<List<Movie>> searchByOperator(Person operator) {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.SEARCH_BY_OPERATOR, operator);
        try {
            DataBaseAnswer<List<Movie>> res = (DataBaseAnswer<List<Movie>>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<List<Movie>>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Void> load() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.LOAD);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }

    public DataBaseAnswer<Void> save() {
        CommandWrapper command = new CommandWrapper(userName, password, CommandName.SAVE);
        try {
            DataBaseAnswer<Void> res = (DataBaseAnswer<Void>) request(host, port, command);
            return res;
        } catch (ClassCastException e) {
            return new DataBaseAnswer<Void>(DataBaseAnswer.CODE_BAD_ANSWER, null);
        }
    }
}
