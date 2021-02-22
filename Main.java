import movie.*;
import database.*;
import interpreter.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        DataBase base = new InternalDataBase();
        DataManipulator manipulator = new DataManipulator(base);
        Person operator = new Person("Denis", "4444", Color.BLACK, Country.RUSSIA);
        Person operator2 = new Person("Denis", "4443", Color.BLACK, Country.RUSSIA);
        Movie movie = new Movie("filmez", new Coordinates(5,2), 1, 233, MovieGenre.COMEDY, MpaaRating.PG_13, operator);
        Movie movie2 = new Movie("filmez2", new Coordinates(-54,2), 3, 754, MovieGenre.HORROR, MpaaRating.PG_13, operator2);
        Movie movie3 = new Movie("filmez3", new Coordinates(-54,2), 3, 754, MovieGenre.HORROR, MpaaRating.PG_13, operator2);
        List<Movie> movies = new ArrayList<Movie>();
        movies.add(movie);
        movies.add(movie2);
        movies.add(movie3);
        manipulator.addAll(movies);
        
        Interpreter interpreter = new CommandInterpreter(manipulator);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            interpreter.interpret(scanner, System.out, true);
        }
    }
}