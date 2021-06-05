import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import userapp.Launcher;

public class GraphicClient extends Application {
    
    private static String host;
    private static int port;
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid arguments. Add host and port number");
            Platform.exit();
            System.exit(0);
            return;
        } else {
            try {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments. Add host and port number");
                Platform.exit();
                System.exit(0);
                return;
            }
        }
        Application.launch();
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Launcher launcher = new Launcher(host, port);
        int code = launcher.launch(mainStage);
        if (code != 0) {
            System.out.println("Error "+Integer.toString(code)+" while loading");
            Platform.exit();
            System.exit(0);
            return;
        } else {
            System.out.println("Loaded");
        }
    }
}
