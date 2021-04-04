
import java.util.Scanner;

import database.DataBaseAnswer;
import database.InternalDataBase;
import interpreter.Interpreter;
import interpreter.MinimalCommandInterpreter;
import objectServer.ObjectSharingServer;
import remoteDataBase.ObjectCommandInterpreter;

public class Server {

    /** default path to save file */
    private final static String DEFAULT_SAVE_PATH = "dataBase.xml";
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

        String saveFilePath;
        if (args.length >= 2) {
            saveFilePath = args[1];
        } else {
            saveFilePath = DEFAULT_SAVE_PATH;
            System.out.printf("[warning] No arguments with save file path. Using default %s\n", saveFilePath);
        }

        InternalDataBase dataBase = new InternalDataBase(saveFilePath);
        ObjectCommandInterpreter interpreter = new ObjectCommandInterpreter(dataBase);
        Interpreter consoleInterpreter = new MinimalCommandInterpreter(dataBase);

        consoleInterpreter.init(new Scanner(System.in), System.out, true);
        dataBase.load();

        ObjectSharingServer server = new ObjectSharingServer(port, 
        obj -> {
            Object res = interpreter.interpret(obj);
            return res;
        },
        consoleInterpreter
        );
        server.run();
    }
}