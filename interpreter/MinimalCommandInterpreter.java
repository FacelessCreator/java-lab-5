package interpreter;

import java.io.PrintStream;
import java.util.Scanner;

import database.DataBase;
import database.DataBaseAnswer;

public class MinimalCommandInterpreter implements Interpreter {
    
    private DataBase dataBase;

    public MinimalCommandInterpreter(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public void init(Scanner in, PrintStream out, boolean isFriendly) {
        out.print("> ");
    }

    public void interpret(Scanner in, PrintStream out, boolean isFriendly) {
        String command = in.nextLine();
        switch (command) {
            case "exit": {
                DataBaseAnswer<Void> dbAnswer = dataBase.save();
                if (dbAnswer.code != 0) {
                    out.println("KO " + Integer.toString(dbAnswer.code));
                } else {
                    out.println("OK");
                }
                out.println("Exiting");
                System.exit(0);
            }
                break;
            case "save": {
                DataBaseAnswer<Void> dbAnswer = dataBase.save();
                if (dbAnswer.code != 0) {
                    out.println("KO " + Integer.toString(dbAnswer.code));
                } else {
                    out.println("OK");
                }
            }
                break;
            case "load": {
                DataBaseAnswer<Void> dbAnswer = dataBase.load();
                if (dbAnswer.code != 0) {
                    out.println("KO " + Integer.toString(dbAnswer.code));
                } else {
                    out.println("OK");
                }
            }
                break;
            case "?":
            case "help":
                out.println("? | help - view this page\nsave - saves database to file\nload - loads database from file\nexit - saves database and turns server off");
                break;
            default:
                out.println("Unknown command \""+command+"\". Use ? or help to view list of commands");
                break;
        }
        out.print("> ");
    }
}
