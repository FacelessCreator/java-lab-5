import java.util.function.UnaryOperator;

import database.InternalDataBase;
import objectServer.ObjectSharingServer;
import remoteDataBase.ObjectCommandInterpreter;

public class Server {
    public static void main(String[] args) {

        InternalDataBase dataBase = new InternalDataBase();
        ObjectCommandInterpreter interpreter = new ObjectCommandInterpreter(dataBase);

        ObjectSharingServer server = new ObjectSharingServer(8080, obj -> {
            Object res = interpreter.interpret(obj);
            return res;
        });
        server.run();
    }
}