package core;

import java.util.Scanner;

public class ConsoleCommandHandler {

    Engine engine;

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
                if(engine.getDiscEngine().isRunning()){
                    engine.getDiscEngine().getFilesHandler().saveAllBotFiles();
                }
                break;

            case "load":
                engine.loadProperties();
                if(engine.getDiscEngine().isRunning()){
                    engine.getDiscEngine().getFilesHandler().loadAllBotFiles();
                }
                break;
            case "debug":
                if (engine.getProperties().debug) {
                    engine.getProperties().debug = false;
                } else {
                    engine.getProperties().debug = true;
                }
                System.out.println("Debug is now " + engine.getProperties().debug);
                break;

            case "showtime":
                if (engine.getProperties().showTime) {
                    engine.getProperties().showTime = false;
                } else {
                    engine.getProperties().showTime = true;
                }
                System.out.println("Show time is now " + engine.getProperties().showTime);
                break;

            case "startbot":
                engine.discApplicationEngine.startBotApplication();
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

            case "help":
                System.out.println("savespeed <speed> - changes speed of save intervall...lol (minutes)\nload - loads all files (override)\nsave - saves all files\nstartBot - starts the bot...UwU\nstopBot - stops the bot\n<tele/disc>token <token> - sets api token\ntelename <name> - sets Name of the Telegram bot\ndebug - turns on debug mode to see more\nshowtime - shows time at console output");
                break;

            default:
                System.out.println("unknown command! Use \"help\" to help...yourself :D");
                break;
        }
    }

    private class SystemInListener implements Runnable {

        @Override
        public void run() {
            String line;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                line = scanner.nextLine();
                handleConsoleCommand(line);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String extractMessage(String extractor, int startAt){
        String message = "";
        String[] commandSplit = extractor.split(" ");
        if (commandSplit.length > startAt) {
            message = commandSplit[startAt];
            for (int i = startAt+1; i < commandSplit.length; i++) {
                if(commandSplit[i].endsWith("\\n"))
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
}