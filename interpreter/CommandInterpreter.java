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

import org.xml.sax.SAXException;

import database.DataBase;
import database.DataBaseAnswer;
import database.DataManipulator;
import movie.*;

/**
 * Interpreter of console commands
 * @author Alexandr Shchukin
 * @version 1.0
 */
public class CommandInterpreter implements Interpreter {

    /** attached data manipulator */
    private DataManipulator manipulator;

    /** commands history */
    protected List<String> history;

    /** path to help description file */
    protected final static String HELP_FILEPATH = "help.txt";

    /** level of recursion (increases when user or script uses execute_script command) */
    protected int recursionLevel = 0;
    
    /** flag to prevent unexpected recursion */
    protected boolean allowRecursion = false;

    /**
     * read help page
     * @return help page or warning message
     */
    protected String readHelp() {

        File helpFile = new File(HELP_FILEPATH);
        if (!helpFile.exists())
            return "[error] Reading "+HELP_FILEPATH+" failed. File doesn't exist. Nobody helps you";
        if (!helpFile.canRead())
            return "[error] Reading "+HELP_FILEPATH+" failed. Permission denied";
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

    /**
     * interpret enum from input stream
     * @param <E> enum class
     * @param clazz enum class
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @param comment message for user to understand what input program is waiting for
     * @param allowNull if true, empty string is interpreted as null and result can be null
     * @return enum object or null
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected <E extends Enum<E>> E interpretEnum(Class<E> clazz, Scanner in, PrintStream out, boolean isFriendly, String comment, boolean allowNull) throws InterpretingFailedException, EOFException {
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
            if (enteredLine.length() == 0 && allowNull) {
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

    /**
     * interpret long from input stream
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @param comment message for user to understand what input program is waiting for
     * @param predicate link to the method that checks if input is correct
     * @param allowNull if true, empty string is interpreted as null and result can be null
     * @return long object or null
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected Long interpretLong(Scanner in, PrintStream out, boolean isFriendly, String comment, Predicate<Long> predicate, boolean allowNull) throws InterpretingFailedException, EOFException {
        do {
            if (isFriendly) {
                out.printf("%s\n* ", comment);
            }
            if (!in.hasNextLine()) {
                throw new EOFException("[error] unexpected EOF");
            }
            String enteredLine = in.nextLine();
            if (enteredLine.length() == 0 && allowNull) {
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

    /**
     * interpret string from input stream
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @param comment message for user to understand what input program is waiting for
     * @param predicate link to the method that checks if input is correct
     * @return string object
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected String interpretString(Scanner in, PrintStream out, boolean isFriendly, String comment, Predicate<String> predicate) throws InterpretingFailedException, EOFException {
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

    /**
     * interpret person from input stream
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @param allowNull if true, empty string is interpreted as null and result can be null
     * @return person object or null
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected Person interpretPerson(Scanner in, PrintStream out, boolean isFriendly, boolean allowNull) throws InterpretingFailedException, EOFException {
        String name = interpretString(in, out, isFriendly, "Input name. It mustn't be empty", s -> s != null && s.length() > 0);
        String passportId = interpretString(in, out, isFriendly, "Input passpord Id. It's more than 3 symbols or be empty. Press ENTER to left it empty", s -> s.length() == 0 || s.length() >= 4);
        Color eyeColor = interpretEnum(Color.class, in, out, isFriendly, "Input eye color. Press ENTER to left it null", true);
        Country nationality = interpretEnum(Country.class, in, out, isFriendly, "Input nationality", false);
        return new Person(name, passportId, eyeColor, nationality);
    }

    /**
     * interpret coordinates from input stream
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @param allowNull if true, empty string is interpreted as null and result can be null
     * @return coordinates object or null
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected Coordinates interpretCoordinates(Scanner in, PrintStream out, boolean isFriendly, boolean allowNull) throws InterpretingFailedException, EOFException {
        int x = interpretLong(in, out, isFriendly, "Input x > -746", i -> i > -746, false).intValue();
        long y = interpretLong(in, out, isFriendly, "Input y > -951", l -> l > -951, false).longValue();
        return new Coordinates(x, y);
    }

    /**
     * interpret movie from input stream
     * @param in input stream
     * @param out output stream
     * @param isFriendly if true, interpreter works in user friendly mode. It has more output and allows to fix incorrect input
     * @return movie object
     * @throws InterpretingFailedException
     * @throws EOFException
     */
    protected Movie interpretMovie(Scanner in, PrintStream out, boolean isFriendly) throws InterpretingFailedException, EOFException {
        String name = interpretString(in, out, isFriendly, "Input name", s -> s.length() > 0);
        Coordinates coordinates = interpretCoordinates(in, out, isFriendly, false);
        long oscarsCount = interpretLong(in, out, isFriendly, "Input oscars count; It must be more than 0", l -> l > 0, false).longValue();
        long length = interpretLong(in, out, isFriendly, "Input length; It must be more than 0", l -> l > 0, false).longValue();
        MovieGenre genre = interpretEnum(MovieGenre.class, in, out, isFriendly, "Input movie genre. Press ENTER to left it null", true);
        MpaaRating mpaaRating = interpretEnum(MpaaRating.class, in, out, isFriendly, "Input rating. Press ENTER to left it null", true);
        Person operator = interpretPerson(in, out, isFriendly, true);
        return new Movie(name, coordinates, oscarsCount, length, genre, mpaaRating, operator);
    }

    /**
     * Execute file as commands stream
     * @param out output stream
     * @param filePath path to the file
     */
    protected void executeFile(PrintStream out, String filePath) {

        if (recursionLevel > 1 && !allowRecursion) {
            out.println("[warning] Recursion denied. Use flag -r to ignore it");
            return;
        }

        File scriptFile = new File(filePath);
        if (!scriptFile.exists()) {
            out.printf("[warning] File %s not found\n", filePath);
            return;
        }
        if (!scriptFile.canRead()) {
            out.printf("[warning] Can't read file %s - permission denied\n", filePath);
            return;
        }
        Scanner fileReader;
        try {
            fileReader = new Scanner(scriptFile);
        } catch (FileNotFoundException e) {
            out.println("[error] this is not possible");
            return;
        }

        while (fileReader.hasNextLine())
        {
            interpret(fileReader, out, false);
        }
    }

    /**
     * Constructor
     * @param manipulator data base manipulator
     * @param saveFilePath path to save and load XML
     */
    public CommandInterpreter(DataManipulator manipulator) {
        this.manipulator = manipulator;
        history = new ArrayList<String>();
    }

    public void init(Scanner in, PrintStream out, boolean isFriendly) {
        
    }

    public void interpret(Scanner in, PrintStream out, boolean isFriendly) {

        ++recursionLevel;

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
            --recursionLevel;
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
            {
                DataBaseAnswer<String> answer = manipulator.getInfo();
                if (answer.code == 0) {
                    out.println(answer.object);
                } else {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                }
            }
                break;
            case "show":
            {
                DataBaseAnswer<List<Movie>> answer = manipulator.getAll();
                if (answer.code != 0) {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                } else {
                    for (Movie movie : answer.object) {
                        out.println(movie);
                    }
                }
            }
                break;

            case "clear":
            {
                DataBaseAnswer<Void> answer = manipulator.clear();
                if (answer.code != 0) {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                }
            }
                break;

            case "exit":
            {
                /*DataBaseAnswer<Void> answer = manipulator.save();
                if (answer.code != 0) {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                }*/
                System.exit(0);
            }
                break;

            case "sum_of_oscars_count":
            {
                DataBaseAnswer<Long> answer = manipulator.getSumOfOscarsCount();
                if (answer.code != 0) {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                } else {
                    out.println(answer.object.longValue());
                }
            }
                break;

            case "group_counting_by_oscars_count":
            {
                DataBaseAnswer<HashMap<Long, Long>> answer = manipulator.getGroupCountingByOscarsCount();
                if (answer.code != 0) {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                } else {
                    for (Long key : answer.object.keySet())
                    {
                        System.out.printf("%d oscars : %d films\n", key.longValue(), answer.object.get(key).longValue());
                    }
                }
            }
            break;
            
            case "add":
            {
                try {
                    Movie movie = interpretMovie(in, out, isFriendly);
                    DataBaseAnswer<Long> answer = manipulator.add(movie);
                    if (answer.code != 0) {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    } else {
                        out.printf("Object added; id: %d\n", answer.object.longValue());
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
                            DataBaseAnswer<Void> answer = manipulator.replace(id, movie);
                            if (answer.code != 0) {
                                out.println(DataBaseAnswer.describeAnswerCode(answer.code));
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
                        DataBaseAnswer<Void> answer = manipulator.remove(id);
                        if (answer.code != 0) {
                            out.println(DataBaseAnswer.describeAnswerCode(answer.code));
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
                    DataBaseAnswer<Boolean> answer = manipulator.addIfMax(movie);
                    if (answer.code != 0) {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    } else {
                        if (!answer.object.booleanValue()) {
                            out.println("[normal] your object is not maximum");
                        }
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
                    DataBaseAnswer<Void> answer = manipulator.removeLower(movie);
                    if (answer.code != 0) {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    }
                } catch (EOFException | InterpretingFailedException e) {
                    out.println(e.getMessage());
                }
            }
            break;

            case "remove_all_by_operator":
            {
                try {
                    Person operator = interpretPerson(in, out, isFriendly, false);
                    DataBaseAnswer<Void> answer = manipulator.removeAllByOperator(operator);
                    if (answer.code != 0) {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    }
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
                    if (recursionLevel == 1) {
                        if (args.size() >= 3 && args.get(2).equals("-r")) {
                            allowRecursion = true;
                        } else {
                            allowRecursion = false;
                        }
                    }
                    
                    executeFile(out, args.get(1));
                }
            }
            break;

            default:
                if (isFriendly)
                    out.printf("unknown command \"%s\"\nuse help or ? to view list of commands\n", commandName);
                break;
        }
        
        --recursionLevel;

    }

    

}
