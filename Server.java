
import java.util.Scanner;

import SQLDataBase.PostgreSQLDataBase;
import SQLDataBase.UserAgent;
import database.DataBaseAnswer;
import database.InternalDataBase;
import interpreter.Interpreter;
import interpreter.MinimalCommandInterpreter;
import objectServer.ObjectSharingServer;
import remoteDataBase.ObjectCommandInterpreter;

public class Server {

    public static void main(String[] args) {

        int port;
        String dataBaseURL;
        String userName;
        String password;

        if (args.length < 4) {
            System.out.println("Invalid arguments. Use: dataBaseURL userName password serverPort");
            return;
        } else {
            try {
                dataBaseURL = args[0];
                userName = args[1];
                password = args[2];
                port = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments. Add port number");
                return;
            }
        }

        PostgreSQLDataBase dataBase = new PostgreSQLDataBase(dataBaseURL, userName, password);
        UserAgent userAgent = new UserAgent(dataBaseURL, userName, password);
        ObjectCommandInterpreter objectInterpreter = new ObjectCommandInterpreter(dataBase, userAgent);

        ObjectSharingServer server = new ObjectSharingServer(port, 
        obj -> {
            Object res = objectInterpreter.interpret(obj);
            return res;
        }
        );
        server.start();
    }
}