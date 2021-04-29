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

import database.ProtectedDataManipulator;

public class ProtectedCommandInterpreter extends CommandInterpreter {
    
    private ProtectedDataManipulator manipulator;

    public ProtectedCommandInterpreter(ProtectedDataManipulator manipulator) {
        super(manipulator);
        this.manipulator = manipulator;
    }

    @Override
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

            case "whoami":
            {
                DataBaseAnswer<String> answer = manipulator.whoami();
                if (answer.code == 0) {
                    out.println(answer.object);
                } else {
                    out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                }
            }
            break;

            case "login":
            {
                if (args.size() < 3) {
                    if (isFriendly) {
                        out.println("wrong arguments");
                    }
                } else {
                    DataBaseAnswer<Void> answer = manipulator.login(args.get(1), args.get(2));
                    if (answer.code == 0) {
                        out.println("success");
                    } else {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    }
                }
            }
            break;

            case "register":
            {
                if (args.size() < 3) {
                    if (isFriendly) {
                        out.println("wrong arguments");
                    }
                } else {
                    DataBaseAnswer<Void> answer = manipulator.register(args.get(1), args.get(2));
                    if (answer.code == 0) {
                        out.println("success");
                    } else {
                        out.println(DataBaseAnswer.describeAnswerCode(answer.code));
                    }
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
