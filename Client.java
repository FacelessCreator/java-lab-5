import movie.*;
import remoteDataBase.RemoteDataBase;
import database.*;
import interpreter.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class Client {

    /** default path to save file */
    private final static String DEFAULT_SAVE_PATH = "dataBase.xml";

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        
        String saveFilePath;
        if (args.length > 0) {
            saveFilePath = args[0];
        } else {
            saveFilePath = DEFAULT_SAVE_PATH;
            System.out.printf("[warning] No arguments with save file path. Using default %s. View help to understand how to change save file path\n", saveFilePath);
        }

        RemoteDataBase dataBase = new RemoteDataBase("localhost", 8080);
        DataManipulator dataManipulator = new DataManipulator(dataBase);
        Interpreter interpreter = new CommandInterpreter(dataManipulator, saveFilePath);
        Scanner scanner = new Scanner(System.in);
        interpreter.init(scanner, System.out, true);
        while (true) {
            interpreter.interpret(scanner, System.out, true);
        }
    }
}