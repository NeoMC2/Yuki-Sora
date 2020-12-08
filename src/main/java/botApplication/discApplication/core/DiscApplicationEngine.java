package botApplication.discApplication.core;

import botApplication.discApplication.commands.*;
import botApplication.discApplication.librarys.DiscApplicationFilesHandler;
import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscRole;
import botApplication.discApplication.librarys.certification.DiscCertificationHandler;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.listeners.*;
import botApplication.discApplication.utils.ApiManager;
import botApplication.discApplication.utils.DiscTextUtils;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscApplicationEngine {

    private final String consMsgDef = "[Discord application]";
    private final Engine engine;
    private final DiscCommandHandler commandHandler = new DiscCommandHandler();
    private final DiscCommandParser commandParser = new DiscCommandParser();
    private final ArrayList<FightHandler> fightHandlers = new ArrayList<>();
    private boolean isRunning = false;
    private ApiManager apiManager;
    private DiscTextUtils textUtils;
    private DiscApplicationFilesHandler filesHandler;
    private DiscUtilityBase utilityBase;
    private DiscCertificationHandler certificationHandler;
    private JDABuilder builder;
    private JDA botJDA;
    private DiscCmdVote voteCmd;
    private HashMap<String, ArrayList<DiscRole.RoleType>> setupRoles = new HashMap<>();

    public DiscApplicationEngine(Engine engine) {
        this.engine = engine;
    }

    public void startBotApplication() {
        if (isRunning) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - bot is already running!!!", false);
            return;
        }
        if (engine.getProperties().discBotApplicationToken == null) {
            if (engine.getProperties().discBotApplicationToken.equalsIgnoreCase("")) {
                engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - token invalid!!!", false);
                return;
            }
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot start initialized!", false);
        isRunning = true;

        initPreCmds();

        textUtils = new DiscTextUtils(engine);
        utilityBase = new DiscUtilityBase(engine);
        certificationHandler = new DiscCertificationHandler(engine);

        builder = JDABuilder.createDefault(engine.getProperties().discBotApplicationToken);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        addBotCommands();
        addBotListeners();
        try {
            botJDA = builder.build();
        } catch (LoginException e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - maybe token invalid!!!", false);
            isRunning = false;
            return;
        }

        apiManager = new ApiManager(engine);
        filesHandler = new DiscApplicationFilesHandler(engine);
        filesHandler.loadAllBotFiles();
        updateAllServerStats();
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot successfully started!", false);
    }

    private void initPreCmds() {
        engine.getUtilityBase().printOutput(consMsgDef + " !Init pre CMDS!", false);
        voteCmd = new DiscCmdVote(engine);
    }

    private void addBotCommands() {
        engine.getUtilityBase().printOutput(consMsgDef + " !Add commands!", false);
        commandHandler.createNewCommand("setup", new DiscCmdSetup(engine));
        commandHandler.createNewCommand("autochan", new DiscCmdAutoChannel());
        commandHandler.createNewCommand("move", new DiscCmdMove());
        commandHandler.createNewCommand("vote", voteCmd);
        commandHandler.createNewCommand("rps", new DiscCmdRockPaperScissors());
        commandHandler.createNewCommand("help", new DiscCmdHelp());
        commandHandler.createNewCommand("job", new DiscCmdJob());
        commandHandler.createNewCommand("wallet", new DiscCmdWallet());
        commandHandler.createNewCommand("monster", new DiscCmdMonster());
        commandHandler.createNewCommand("m", new DiscCmdMusic());
        commandHandler.createNewCommand("item", new DiscCmdItem());
        commandHandler.createNewCommand("oven", new DiscCmdOven());
        commandHandler.createNewCommand("bait", new DiscCmdBait());
    }

    private void addBotListeners() {
        engine.getUtilityBase().printOutput(consMsgDef + " !Add listeners!", false);
        builder.addEventListeners(new DiscMessageListener(engine));
        builder.addEventListeners(new DiscBotJoinListener(engine));
        builder.addEventListeners(new DiscReactionListener(engine));
        builder.addEventListeners(new DiscChannelAddListener(engine));
        builder.addEventListeners(new DiscVoiceListener(engine));
    }

    public void updateAllServerStats() {
        for (Object s : filesHandler.getServers().values().toArray()) {
            DiscApplicationServer se = (DiscApplicationServer) s;
            se.updateServerStats(engine);
        }
    }

    public void shutdownBotApplication() {
        if (!isRunning) {
            engine.getUtilityBase().printOutput(consMsgDef + " ~The bot is already offline!", false);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " ~Bot shutting down!", false);
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

    public ArrayList<FightHandler> getFightHandlers() {
        return fightHandlers;
    }

    public JDA getBotJDA() {
        return botJDA;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }
}
