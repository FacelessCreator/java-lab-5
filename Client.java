import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import database.DataBase;
import database.DataManipulator;
import movie.Color;
import movie.Coordinates;
import movie.Country;
import movie.Movie;
import movie.MovieGenre;
import movie.MpaaRating;
import movie.Person;
import remoteDataBase.RemoteDataBase;
import etc.Serializing;
import interpreter.CommandInterpreter;
import interpreter.Interpreter;

public class Client {

    /** default path to save file */
    private final static String DEFAULT_SAVE_PATH = "dataBase.xml";
    public static void main(String[] args) throws ClassNotFoundException {

        String saveFilePath;
        if (args.length > 0) {
            saveFilePath = args[0];
        } else {
            saveFilePath = DEFAULT_SAVE_PATH;
            System.out.printf("[warning] No arguments with save file path. Using default %s. View help to understand how to change save file path\n", saveFilePath);
        }

        DataBase dataBase = new RemoteDataBase("localhost", 8080);
        DataManipulator dataManipulator = new DataManipulator(dataBase);
        Interpreter interpreter = new CommandInterpreter(dataManipulator, saveFilePath);
        Scanner scanner = new Scanner(System.in);
        interpreter.init(scanner, System.out, true);
        while (true) {
            interpreter.interpret(scanner, System.out, true);
        }
        
        /*try {
            Socket socket = new Socket("localhost", 5454);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            Movie obj = new Movie("Arch", new Coordinates(1, 2), 4, 500, MovieGenre.COMEDY, MpaaRating.PG_13, new Person("Gregor", "1234", Color.BROWN, Country.ITALY));
            DataOutputStream out = new DataOutputStream(outputStream);
            byte[] objData = Serializing.serializeObject(obj);
            out.writeInt(objData.length);
            outputStream.write(objData);
            outputStream.flush();
            
            DataInputStream in = new DataInputStream(inputStream);
            int answerLength = in.readInt();
            byte[] answerData = in.readAllBytes();
            Object answerObject = Serializing.deserializeObject(answerData);
            Movie answer = (Movie) answerObject;
            System.out.println(answer);
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }*/
    }

}
