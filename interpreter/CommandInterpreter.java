package interpreter;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import database.DataManipulator;
import movie.*;

public class CommandInterpreter implements Interpreter {

    private DataManipulator manipulator;
    private List<String> history;
    private final static String HELP_FILEPATH = "help.txt";
    private final static String SAVE_FILEPATH = "dataBase.xml";

    private String readHelp() {

        File helpFile = new File(HELP_FILEPATH);
        if (!helpFile.exists())
            return "[warning] Nobody helps you";
        if (!helpFile.canRead())
            return "[warning] Help reading failed";
        Scanner fileReader;
        try {
            fileReader = new Scanner(helpFile);
        } catch (FileNotFoundException e) {
            return "[error] This is impossible";
        }
        StringBuilder res = new StringBuilder();
        while (fileReader.hasNextLine())
        {
            res.append(fileReader.nextLine()).append("\n");
        }
        fileReader.close();
        return res.toString();
    }

    private <E extends Enum<E>> E interpretEnum(Class<E> clazz, Scanner in, PrintStream out, boolean isFriendly, String comment, boolean allowNull) throws InterpretingFailedException, EOFException {
        E res;
        List<E> variants = Arrays.asList(clazz.getEnumConstants());
        StringBuilder variantsString = new StringBuilder();
        for (E variant : variants) {
            variantsString.append(variant.name()).append(" ");
        }
        do {
            if (isFriendly) {
                out.println(comment);
                out.printf("Chose one of %s\n* ", variantsString);
            }
            if (!in.hasNextLine()) {
                throw new EOFException("[error] unexpected EOF");
            }
            String enteredLine = in.nextLine();
            if (enteredLine == "" && allowNull) {
                return null;
            }
            try {
                res = Enum.valueOf(clazz, enteredLine);
                return res;
            } catch (IllegalArgumentException e) {
                if (isFriendly) {
                    out.printf("There is no variant \"%s\"\n", enteredLine);
                }
            }
        } while (isFriendly);
        throw new InterpretingFailedException("[error] cannot understand line");
    }

    private Long interpretLong(Scanner in, PrintStream out, boolean isFriendly, String comment, Predicate<Long> predicate, boolean allowNull) throws InterpretingFailedException, EOFException {
        do {
            if (isFriendly) {
                out.printf("%s\n* ", comment);
            }
            if (!in.hasNextLine()) {
                throw new EOFException("[error] unexpected EOF");
            }
            String enteredLine = in.nextLine();
            if (enteredLine == "" && allowNull) {
                return null;
            }
            try {
                Long res = Long.valueOf(enteredLine);
                if (!predicate.test(res)) {
                    throw new NumberFormatException();
                }
                return res;
            } catch (NumberFormatException e) {
                if (isFriendly) {
                    out.println("Wrong input");
                }
            }
        } while (isFriendly);
        throw new InterpretingFailedException("[error] cannot understand line");
    }

    private String interpretString(Scanner in, PrintStream out, boolean isFriendly, String comment, Predicate<String> predicate) throws InterpretingFailedException, EOFException {
        do {
            if (isFriendly) {
                out.printf("%s\n* ", comment);
            }
            if (!in.hasNextLine()) {
                throw new EOFException("[error] unexpected EOF");
            }
            String enteredLine = in.nextLine();
            if (!predicate.test(enteredLine)) {
                if (isFriendly) {
                    out.println("Wrong input");
                }
            } else {
                return enteredLine;
            }
        } while (isFriendly);
        throw new InterpretingFailedException("[error] cannot understand line");
    }

    private Person interpretPerson(Scanner in, PrintStream out, boolean isFriendly, boolean allowNull) throws InterpretingFailedException, EOFException {
        String name = interpretString(in, out, isFriendly, "Input name. It mustn't be empty", s -> s != null && s.length() > 0);
        String passportId = interpretString(in, out, isFriendly, "Input passpord Id. It's more than 3 symbols or be empty. Press ENTER to left it empty", s -> s == "" || s.length() >= 4);
        Color eyeColor = interpretEnum(Color.class, in, out, isFriendly, "Input eye color. Press ENTER to left it null", true);
        Country nationality = interpretEnum(Country.class, in, out, isFriendly, "Input nationality", false);
        return new Person(name, passportId, eyeColor, nationality);
    }

    private Coordinates interpretCoordinates(Scanner in, PrintStream out, boolean isFriendly, boolean allowNull) throws InterpretingFailedException, EOFException {
        int x = interpretLong(in, out, isFriendly, "Input x > -746", i -> i > -746, false).intValue();
        long y = interpretLong(in, out, isFriendly, "Input y > -951", l -> l > -951, false).longValue();
        return new Coordinates(x, y);
    }

    private Movie interpretMovie(Scanner in, PrintStream out, boolean isFriendly) throws InterpretingFailedException, EOFException {
        String name = interpretString(in, out, isFriendly, "Input name", s -> s.length() > 0);
        Coordinates coordinates = interpretCoordinates(in, out, isFriendly, false);
        long oscarsCount = interpretLong(in, out, isFriendly, "Input oscars count; It must be more than 0", l -> l > 0, false).longValue();
        long length = interpretLong(in, out, isFriendly, "Input length; It must be more than 0", l -> l > 0, false).longValue();
        MovieGenre genre = interpretEnum(MovieGenre.class, in, out, isFriendly, "Input movie genre. Press ENTER to left it null", true);
        MpaaRating mpaaRating = interpretEnum(MpaaRating.class, in, out, isFriendly, "Input rating. Press ENTER to left it null", true);
        Person operator = interpretPerson(in, out, isFriendly, true);
        return new Movie(name, coordinates, oscarsCount, length, genre, mpaaRating, operator);
    }

    private void executeFile(PrintStream out, String filePath) {
        File scriptFile = new File(filePath);
        if (!scriptFile.exists()) {
            out.println("file not fount");
            return;
        }
        if (!scriptFile.canRead()) {
            out.println("cannot read file");
            return;
        }
        Scanner fileReader;
        try {
            fileReader = new Scanner(scriptFile);
        } catch (FileNotFoundException e) {
            out.println("[error] this is not impossible");
            return;
        }

        while (fileReader.hasNextLine())
        {
            interpret(fileReader, out, false);
        }
    }

    private void saveXML(PrintStream out, String filePath, List<Movie> movies) {
        Movies wrappedMovies = new Movies();
        wrappedMovies.setMovies(movies);
        try {
            JAXBContext context = JAXBContext.newInstance(Movies.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File xmlFile = new File(filePath);
            marshaller.marshal(wrappedMovies, xmlFile);
        } catch (JAXBException e) {
            out.print("[error]");
            out.println(e);
        }
    }

    private boolean checkCoordinates(Coordinates coordinates) {
        return coordinates.getX() > -746 && coordinates.getY() > -951;
    }

    private boolean checkOperator(Person operator) {
        String name = operator.getName();
        String passportId = operator.getPassportId();
        Country nationality = operator.getNationality();
        return 
            name != null 
            && name.length() > 0 
            && (passportId == null || passportId.length() >= 4)
            && nationality != null;
    }

    private boolean checkMovie(Movie movie) {
        String name = movie.getName();
        long oscarsCount = movie.getOscarsCount();
        long length = movie.getLength();
        Coordinates coordinates = movie.getCoordinates();
        Person operator = movie.getOperator();
        return
            name != null
            && name.length() > 0
            && coordinates != null
            && checkCoordinates(coordinates)
            && oscarsCount > 0
            && length > 0
            && (operator == null || checkOperator(operator));
    }

    private List<Movie> loadXML(PrintStream out, String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Movies.class);
            Unmarshaller un = jaxbContext.createUnmarshaller();
            File xmlFile = new File(filePath);
            Movies wrappedMovies = (Movies) un.unmarshal(xmlFile);
            List<Movie> movies = new ArrayList<Movie>();
            for (Movie movie : wrappedMovies.getMovies()) {
                if (checkMovie(movie)) {
                    movies.add(movie);
                }
            }
            return movies;
        } catch (JAXBException e) {
            out.print("[error]");
            out.println(e);
            return null;
        }
    }

    public CommandInterpreter(DataManipulator manipulator) {
        this.manipulator = manipulator;
        history = new ArrayList<String>();
    }

    public void interpret(Scanner in, PrintStream out, boolean isFriendly) {

        if (isFriendly) {
            out.print("> ");
        }

        if (!in.hasNextLine()) {
            System.exit(0);
        }

        String enteredLine = in.nextLine();
        List<String> args = new ArrayList<String>(Arrays.asList(enteredLine.split(" ")));

        Iterator<String> it = args.iterator();
        while (it.hasNext())
        {
            String arg = it.next();
            if (arg == "") {
                it.remove();
            }
        }

        /*out.printf("Count of args: %d\n", args.size());
        for (String arg : args) {
            out.printf("arg [%s]\n", arg);
        }
        out.println();*/

        if (args.size() == 0) {
            return;
        }

        String commandName = args.get(0);

        history.add(commandName);

        switch (commandName) {
            case "?": case "help":
                out.println(readHelp());
                break;

            case "history":
                int startPoint = Math.max(0, history.size()-9);
                if (args.size() > 1 && args.get(1).equals("-a")) {
                    startPoint = 0;
                }
                for (int i = startPoint; i < history.size(); ++i) {
                    out.printf("%d %s\n", i+1, history.get(i));
                }
                break;

            case "info":
                out.println(manipulator.getInfo());
                break;

            case "show":
            {
                List<Movie> movies = manipulator.getAll();
                for (Movie movie : movies) {
                    out.println(movie);
                }
            }
            break;

            case "clear":
                manipulator.clear();
                break;

            case "exit":
                System.exit(0);
                break;

            case "sum_of_oscars_count":
                out.println(manipulator.getSumOfOscarsCount());
                break;

            case "group_counting_by_oscars_count":
            {
                HashMap<Long, Long> groups = manipulator.getGroupCountingByOscarsCount();
                for (Long key : groups.keySet())
                {
                    System.out.printf("%d oscars : %d films\n", key.longValue(), groups.get(key).longValue());
                }
            }
            break;
            
            case "add":
            {
                try {
                    Movie movie = interpretMovie(in, out, isFriendly);
                    if (manipulator.add(movie) == -1) {
                        out.println("adding failed");
                    }
                } catch (EOFException | InterpretingFailedException e) {
                    out.println(e.getMessage());
                }
            }
            break;

            case "update":
            {
                if (args.size() < 2) {
                    if (isFriendly) {
                        out.println("wrong arguments");
                    }
                } else {
                    try {
                        int id = Integer.valueOf(args.get(1));
                        try {
                            Movie movie = interpretMovie(in, out, isFriendly);
                            boolean res = manipulator.replace(id, movie);
                            if (!res && isFriendly) {
                                out.println("update failed");
                            }
                        } catch (EOFException | InterpretingFailedException e) {
                            out.println(e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        if (isFriendly) {
                            out.println("wrong arguments");
                        }
                    }
                }
            }
            break;

            case "remove_by_id":
            {
                if (args.size() < 2) {
                    if (isFriendly) {
                        out.println("wrong arguments");
                    }
                } else {
                    try {
                        int id = Integer.valueOf(args.get(1));
                        boolean res = manipulator.remove(id);
                        if (!res && isFriendly) {
                            out.println("removing failed");
                        }
                    } catch (NumberFormatException e) {
                        if (isFriendly) {
                            out.println("wrong arguments");
                        }
                    }
                }
            }
            break;

            case "add_if_max":
            {
                try {
                    Movie movie = interpretMovie(in, out, isFriendly);
                    if (!manipulator.addIfMax(movie)) {
                        out.println("adding failed");
                    }
                } catch (EOFException | InterpretingFailedException e) {
                    out.println(e.getMessage());
                }
            }
            break;

            case "remove_lower":
            {
                try {
                    Movie movie = interpretMovie(in, out, isFriendly);
                    manipulator.removeLower(movie);
                } catch (EOFException | InterpretingFailedException e) {
                    out.println(e.getMessage());
                }
            }
            break;

            case "remove_all_by_operator":
            {
                try {
                    Person operator = interpretPerson(in, out, isFriendly, false);
                    manipulator.removeAllByOperator(operator);
                } catch (EOFException | InterpretingFailedException e) {
                    out.println(e.getMessage());
                }
            }
            break;

            case "execute_script":
            {
                if (args.size() < 2) {
                    if (isFriendly) {
                        out.println("wrong arguments");
                    }
                } else {
                    executeFile(out, args.get(1));
                }
            }
            break;

            case "save":
            {
                List<Movie> movies = manipulator.getAll();
                saveXML(out, SAVE_FILEPATH, movies);
            }    
            break;

            case "load":
            {
                List<Movie> movies = loadXML(out, SAVE_FILEPATH);
                if (movies != null) {
                    manipulator.clear();
                    manipulator.addAll(movies);
                }
            }
            break;

            default:
                if (isFriendly)
                    out.printf("unknown command \"%s\"\nuse help or ? to view list of commands\n", commandName);
                break;
        }
        
    }

    

}
