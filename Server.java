import java.util.function.UnaryOperator;

import objectServer.ObjectSharingServer;

public class Server {

    public static void main(String[] args) {
        ObjectSharingServer server = new ObjectSharingServer(8080, obj -> {
            return obj;
        });
        server.run();
    }
}