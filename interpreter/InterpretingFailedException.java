package interpreter;

/**
 * Interpreter method throws this exception if it's not possible to return correct result of interpreting
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class InterpretingFailedException extends Exception {
    /**
     * Constructor
     * @param msg
     */
    public InterpretingFailedException (String msg) {
        super(msg);    
    }
}
