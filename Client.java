import remoteDataBase.RemoteDataBase;
import database.*;
import interpreter.*;

import java.util.Scanner;

/**
 * Main class
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class Client {

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {

        int port;
        if (args.length < 1) {
            System.out.println("Invalid arguments. Add port number");
            return;
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments. Add port number");
                return;
            }
        }

        RemoteDataBase dataBase = new RemoteDataBase("localhost", port);
        ProtectedDataManipulator dataManipulator = new ProtectedDataManipulator(dataBase);
        ProtectedCommandInterpreter interpreter = new ProtectedCommandInterpreter(dataManipulator);
        Scanner scanner = new Scanner(System.in);
        interpreter.init(scanner, System.out, true);
        while (true) {
            interpreter.interpret(scanner, System.out, true);
        }
    }
}