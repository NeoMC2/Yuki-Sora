package botApplication.discApplication.core;

import botApplication.discApplication.commands.DiscCmdSetup;
import botApplication.discApplication.librarys.DiscApplicationFilesHandler;
import botApplication.discApplication.librarys.certification.DiscCertificationHandler;
import botApplication.discApplication.listeners.DiscBotJoinListener;
import botApplication.discApplication.listeners.DiscMessageListener;
import botApplication.discApplication.listeners.DiscReactionListener;
import botApplication.discApplication.utils.DiscTextUtils;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class DiscApplicationEngine {

    private Engine engine;

    private final String consMsgDef = "[Discord application]";
    private boolean isRunning = false;

    private DiscTextUtils textUtils;
    private DiscApplicationFilesHandler filesHandler;
    private DiscUtilityBase utilityBase;

    private DiscCommandHandler commandHandler = new DiscCommandHandler();
    private DiscCommandParser commandParser = new DiscCommandParser();

    private DiscCertificationHandler certificationHandler;

    private JDABuilder builder;
    private JDA botJDA;

    public DiscApplicationEngine(Engine engine) {
        this.engine = engine;
    }

    public void startBotApplication(){
        if(isRunning){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - bot is already running!!!", false);
            return;
        }
        if(engine.getProperties().discBotApplicationToken == null){
            if(engine.getProperties().discBotApplicationToken.equalsIgnoreCase("")){
                engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - token invalid!!!", false);
                return;
            }
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot start initialized!", false);
        isRunning = true;

        filesHandler = new DiscApplicationFilesHandler(engine);
        filesHandler.loadAllBotFiles();

        textUtils = new DiscTextUtils(engine);
        utilityBase = new DiscUtilityBase(engine);
        certificationHandler = new DiscCertificationHandler(engine);

        builder = new JDABuilder(AccountType.BOT);
        builder.setToken(engine.getProperties().discBotApplicationToken);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        setBotApplicationGame(null, Game.GameType.DEFAULT);
        addBotCommands();
        addBotListeners();
        try {
            botJDA = builder.build();
        } catch (LoginException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - maybe token invalid!!!", false);
            isRunning = false;
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot successfully started!", false);
    }

    private void setBotApplicationGame(String game, Game.GameType type) {
        builder.setGame(new Game("") {
            @Override
            public String getName() {
                if (game != null) {
                    return game;
                } else {
                    return engine.getProperties().discBotApplicationGame;
                }
            }

            @Override
            public String getUrl() {
                return null;
            }

            @Override
            public Game.GameType getType() {
                return type;
            }
        });
    }

    private void addBotCommands(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add commands!", false);
        commandHandler.createNewCommand("setup", new DiscCmdSetup());
    }

    private void addBotListeners(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add listeners!", false);
        builder.addEventListener(new DiscMessageListener(engine));
        builder.addEventListener(new DiscBotJoinListener(engine));
        builder.addEventListener(new DiscReactionListener(engine));
    }

    public void shutdownBotApplication() {
        if(!isRunning){
            engine.getUtilityBase().printOutput(consMsgDef + " ~The bot is already offline!", false);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " ~Bot shutting down!",false);
        try {
            botJDA.shutdownNow();
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " ~Bot cant shutdownBotApplication, eventually never starts?", false);
        }
        isRunning = false;
    }

    public DiscTextUtils getTextUtils() {
        return textUtils;
    }

    public DiscApplicationFilesHandler getFilesHandler() {
        return filesHandler;
    }

    public DiscUtilityBase getUtilityBase() {
        return utilityBase;
    }

    public DiscCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public DiscCommandParser getCommandParser() {
        return commandParser;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public DiscCertificationHandler getCertificationHandler() {
        return certificationHandler;
    }
}
