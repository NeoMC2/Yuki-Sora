package core;

import botApplication.discApplication.librarys.DiscApplicationUser;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleCommandHandler {

    Engine engine;
    DiscApplicationUser user = null;

    public ConsoleCommandHandler(Engine engine) {
        this.engine = engine;
        new Thread(new SystemInListener()).start();
    }

    public void handleConsoleCommand(String command) {
        String args0;
        try {
            args0 = command.split(" ")[0];
        } catch (Exception e) {
            return;
        }
        switch (args0.toLowerCase()) {
            case "save":
                engine.saveProperties();
                if (engine.getDiscEngine().isRunning()) {
                    engine.getDiscEngine().getFilesHandler().saveAllBotFiles();
                }
                break;

            case "api":
                engine.getProperties().api = command.split(" ")[1];
                break;

            case "load":
                engine.loadProperties();
                engine.loadLanguage();
                engine.loadPics();
                if (engine.getDiscEngine().isRunning()) {
                    engine.getDiscEngine().getFilesHandler().loadAllBotFiles();
                }
                break;
            case "debug":
                engine.getProperties().debug = !engine.getProperties().debug;
                System.out.println("Debug is now " + engine.getProperties().debug);
                break;

            case "showtime":
                engine.getProperties().showTime = !engine.getProperties().showTime;
                System.out.println("Show time is now " + engine.getProperties().showTime);
                break;

            case "startbot":
                engine.getDiscEngine().startBotApplication();
                break;

            case "stopbot":
                engine.getDiscEngine().shutdownBotApplication();
                break;

            case "teletoken":
                try {
                    engine.getProperties().telBotApplicationToken = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Telegram token", false);
                break;

            case "telename":
                try {
                    engine.getProperties().telBotApplicationName = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Telegram name", false);
                break;

            case "disctoken":
                try {
                    engine.getProperties().discBotApplicationToken = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Discord token", false);
                break;

            case "savespeed":
                try {
                    engine.getProperties().saveSpeed = Integer.valueOf(command.split(" ")[1]);
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    break;
                }
                engine.getUtilityBase().printOutput("Changed save speed!", false);
                break;

            case "stop":
                engine.shutdown();
                break;

            case "makeadmin":
                try {
                    user = engine.getDiscEngine().getFilesHandler().getUserById(command.split(" ")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    engine.getUtilityBase().printOutput("User not found or error!", false);
                    return;
                }
                user.setAdmin(true);
                break;

            case "undoadmin":
                try {
                    user = engine.getDiscEngine().getFilesHandler().getUserById(command.split(" ")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    engine.getUtilityBase().printOutput("User not found or error!", false);
                    return;
                }
                user.setAdmin(false);
                break;

            case "addbotslave":
                if (engine.getProperties().botSlaves == null)
                    engine.getProperties().botSlaves = new ArrayList<>();
                engine.getProperties().botSlaves.add(command.split(" ")[1]);
                System.out.println("added");
                break;

            case "removebotslave":
                if (engine.getProperties().botSlaves == null)
                    engine.getProperties().botSlaves = new ArrayList<>();
                engine.getProperties().botSlaves.remove(command.split(" ")[1]);
                System.out.println("removed");
                break;

            case "help":
                System.out.println("addbotslave <url> - adds music bot slave\nremovebotslave <url> - removes music bot slave\nmakeadmin <user id> - adds admin status\nundoadmin <user id> - removes admin status\nsavespeed <speed> - changes speed of save intervall...lol (minutes)\nload - loads all files (override)\nsave - saves all files\nstartBot - starts the bot...UwU\nstopBot - stops the bot\n<tele/disc>token <token> - sets api token\ntelename <name> - sets Name of the Telegram bot\ndebug - turns on debug mode to see more\nshowtime - shows time at console output");
                break;

            default:
                System.out.println("unknown command! Use \"help\" to help...yourself :D");
                break;
        }
    }

    private String extractMessage(String extractor, int startAt) {
        String message = "";
        String[] commandSplit = extractor.split(" ");
        if (commandSplit.length > startAt) {
            message = commandSplit[startAt];
            for (int i = startAt + 1; i < commandSplit.length; i++) {
                if (commandSplit[i].endsWith("\\n"))
                    message = message + " " + commandSplit[i].replace("\\n", "") + "\n";
                else
                    message = message + " " + commandSplit[i];
            }
        } else {
            engine.getUtilityBase().printOutput("Invalid amount of characters!", false);
            return null;
        }
        return message;
    }

    private class SystemInListener implements Runnable {

        @Override
        public void run() {
            String line;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                line = scanner.nextLine();
                try {
                    handleConsoleCommand(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}