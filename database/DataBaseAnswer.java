package database;

import java.io.Serializable;

public class DataBaseAnswer<T> implements Serializable {

    public static final int CODE_SUCCESS = 0;
    /** message code - object already exists */
    public static final int CODE_OBJECT_ALREADY_EXISTS = 1;
    /** message code - object is not found */
    public static final int CODE_OBJECT_NOT_FOUND = 2;
    
    public static final int CODE_CONNECTION_FAILED = 3;

    public static final int CODE_BAD_ANSWER = 4;

    public static final int CODE_BAD_REQUEST = 5;

    public static final int CODE_PERMISSION_DENIED = 6;

    public static final int CODE_INTERNAL_ERROR = 7;

    public static final int CODE_SQL_ERROR = 8;

    public static final int CODE_OPERATION_UNSUPPORTED = 9;

    public static final int CODE_AUTHORIZING_DENIED = 10;

    public static String describeAnswerCode(int code) {
        String res;
        switch (code) {
            case CODE_SUCCESS:
                res = "All is OK";
                break;
            case CODE_OBJECT_ALREADY_EXISTS:
                res = "[warning] Object already exists";
                break;
            case CODE_OBJECT_NOT_FOUND:
                res = "[warning] Object not found";
                break;
            case CODE_CONNECTION_FAILED:
                res = "[error] Connection failed";
                break;
            case CODE_BAD_ANSWER:
                res = "[error] Bad server answer";
                break;
            case CODE_BAD_REQUEST:
                res = "[error] Bad request";
                break;
            case CODE_PERMISSION_DENIED:
                res = "[error] Permission denied";
                break;
            case CODE_INTERNAL_ERROR:
                res = "[error] Internal error";
                break;
            case CODE_SQL_ERROR:
                res = "[error] SQL error";
                break;
            case CODE_OPERATION_UNSUPPORTED:
                res = "[error] operation unsupported";
                break;
            case CODE_AUTHORIZING_DENIED:
                res = "[error] authorizing denied";
                break;
            default:
                res = "[error] Unknown code";
                break;
        }
        return res;
    }

    public int code;
    public T object;
    public DataBaseAnswer(int code, T object) {
        this.code = code;
        this.object = object;
    }
}
