package interpreter;

import java.io.PrintStream;
import java.util.Scanner;

public interface Interpreter {
    
    void interpret(Scanner in, PrintStream out, boolean isFriendly);

}
