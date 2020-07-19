package botApplication.discApplication.core;

import botApplication.discApplication.commands.*;
import botApplication.discApplication.librarys.DiscApplicationFilesHandler;
import botApplication.discApplication.librarys.DiscRole;
import botApplication.discApplication.librarys.certification.DiscCertificationHandler;
import botApplication.discApplication.listeners.*;
import botApplication.discApplication.transaktion.TransaktionHandler;
import botApplication.discApplication.utils.DiscTextUtils;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscApplicationEngine {

    private Engine engine;

    private final String consMsgDef = "[Discord application]";
    private boolean isRunning = false;

    private DiscTextUtils textUtils;
    private DiscApplicationFilesHandler filesHandler;
    private DiscUtilityBase utilityBase;

    private DiscCommandHandler commandHandler = new DiscCommandHandler();
    private DiscCommandParser commandParser = new DiscCommandParser();
    private TransaktionHandler transaktionHandler;

    private DiscCertificationHandler certificationHandler;

    private JDABuilder builder;
    private JDA botJDA;

    private DiscCmdVote voteCmd;

    private HashMap<String, ArrayList<DiscRole.RoleType>> setupRoles = new HashMap<>();

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

        transaktionHandler = new TransaktionHandler(engine);

        initPreCmds();

        filesHandler = new DiscApplicationFilesHandler(engine);
        filesHandler.loadAllBotFiles();

        textUtils = new DiscTextUtils(engine);
        utilityBase = new DiscUtilityBase(engine);
        certificationHandler = new DiscCertificationHandler(engine);

        builder = new JDABuilder(AccountType.BOT);
        builder.setToken(engine.getProperties().discBotApplicationToken);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        addBotCommands();
        setBotApplicationGame(null, Game.GameType.DEFAULT);
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

    private void initPreCmds(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Init pre CMDS!", false);
        voteCmd = new DiscCmdVote(engine);
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
        commandHandler.createNewCommand("setup", new DiscCmdSetup(engine));
        commandHandler.createNewCommand("autochan", new DiscCmdAutoChannel());
        commandHandler.createNewCommand("move", new DiscCmdMove());
        commandHandler.createNewCommand("vote", voteCmd);
        commandHandler.createNewCommand("rps", new DiscCmdRockPaperScissors());
        commandHandler.createNewCommand("help", new DiscCmdHelp());
        commandHandler.createNewCommand("job", new DiscCmdJob());
        commandHandler.createNewCommand("admin", new DiscCmdAdmin());
        commandHandler.createNewCommand("wallet", new DiscCmdWallet());
    }

    private void addBotListeners(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add listeners!", false);
        builder.addEventListener(new DiscMessageListener(engine));
        builder.addEventListener(new DiscBotJoinListener(engine));
        builder.addEventListener(new DiscReactionListener(engine));
        builder.addEventListener(new DiscChannelAddListener(engine));
        builder.addEventListener(new DiscVoiceListener(engine));
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

    public HashMap<String, ArrayList<DiscRole.RoleType>> getSetupRoles() {
        return setupRoles;
    }

    public void setSetupRoles(HashMap<String, ArrayList<DiscRole.RoleType>> setupRoles) {
        this.setupRoles = setupRoles;
    }
    public void addSetupRole(String guildId, ArrayList<DiscRole.RoleType> arr) {
        setupRoles.remove(guildId);
        setupRoles.put(guildId, arr);
    }

    public DiscCmdVote getVoteCmd() {
        return voteCmd;
    }

    public TransaktionHandler getTransaktionHandler() {
        return transaktionHandler;
    }
}
