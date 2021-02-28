package interpreter;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Interface of all interpreters
 * @author Alexandr Shchukin
 * @version 1.0
 */
public interface Interpreter {
    
    /**
     * Initialization
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     */
    public void init(Scanner in, PrintStream out, boolean isFriendly);

    /**
     * Interpret stream
     * @param in input stream
     * @param out output strean
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     */
    void interpret(Scanner in, PrintStream out, boolean isFriendly);

}
