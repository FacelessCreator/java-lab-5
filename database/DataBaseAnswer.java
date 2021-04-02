package database;

import java.io.Serializable;

public class DataBaseAnswer<T> implements Serializable {

    public static final int CODE_SUCCESS = 0;
    /** message code - object already exists */
    public static final int CODE_OBJECT_ALREADY_EXISTS = -1;
    /** message code - object is not found */
    public static final int CODE_OBJECT_NOT_FOUND = -2;
    
    public static final int CODE_CONNECTION_FAILED = -3;

    public static final int CODE_BAD_ANSWER = -4;

    public static final int CODE_BAD_REQUEST = -5;

    public int code;
    public T object;
    public DataBaseAnswer(int code, T object) {
        this.code = code;
        this.object = object;
    }
}
