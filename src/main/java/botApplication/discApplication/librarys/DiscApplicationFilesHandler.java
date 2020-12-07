package botApplication.discApplication.librarys;

import botApplication.discApplication.commands.DiscCmdBait;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.monsters.Monster;
import botApplication.discApplication.librarys.job.Job;
import botApplication.discApplication.librarys.poll.Poll;
import botApplication.discApplication.librarys.transaktion.TransaktionHandler;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscApplicationFilesHandler {

    private final Engine engine;
    private HashMap<String, DiscApplicationServer> servers = new HashMap<>();
    private HashMap<String, DiscApplicationUser> users = new HashMap<>();
    private ArrayList<Job> jobs = new ArrayList<>();
    private ArrayList<Monster> monsters;
    private final HashMap<String, Dungeon> dungeons = new HashMap<>();
    private final ArrayList<DiscCmdBait.Bait> baits = new ArrayList<>();

    public DiscApplicationFilesHandler(Engine engine) {
        this.engine = engine;
    }

    public DiscApplicationServer getServerById(String id) {
        DiscApplicationServer server = null;
        if (servers.containsKey(id)) {
            server = servers.get(id);
        }
        return server;
    }

    public DiscApplicationUser getUserById(String id) {
        DiscApplicationUser user = null;
        if (users.containsKey(id)) {
            user = users.get(id);
        }
        return user;
    }

    public DiscApplicationServer createNewServer(Guild guild) {
        if (servers.containsKey(guild.getId())) {
            engine.getUtilityBase().printOutput("Server already exist! Id: " + guild.getId() + " name: " + guild.getName(), true);
            return servers.get(guild.getId());
        }
        DiscApplicationServer server = new DiscApplicationServer(guild);
        servers.put(guild.getId(), server);
        return server;
    }

    public DiscApplicationUser createNewUser(User user, DiscCertificationLevel discCertificationLevel) {
        if (users.containsKey(user.getId())) {
            engine.getUtilityBase().printOutput("User already exist! Id: " + user.getId() + " name: " + user.getName(), true);
            return users.get(user.getId());
        }
        DiscApplicationUser botUser = new DiscApplicationUser(user, discCertificationLevel);
        users.put(user.getId(), botUser);
        return botUser;
    }

    public void loadAllBotFiles() {
        engine.getUtilityBase().printOutput("~load all bot files!", true);

        try {
            monsters = TransaktionHandler.parseJsonToMonster(engine.getFileUtils().loadJsonFile(engine.getFileUtils().home + "/transactions/monsters.json"));
        } catch (Exception e) {
            e.printStackTrace();
            engine.getUtilityBase().printOutput("!!Monsers cant load!!", true);
            e.printStackTrace();
        }
        try {
            jobs = TransaktionHandler.parseJsonToJobs(engine.getFileUtils().loadJsonFile(engine.getFileUtils().home + "/transactions/jobs.json"));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Jobs cant load!!", true);
            e.printStackTrace();
        }
        try {
            engine.getDiscEngine().getVoteCmd().setPolls((ArrayList<Poll>) engine.getFileUtils().loadObject(engine.getFileUtils().home + "/vote/votes.dat"));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Votes cant load!!", true);
            e.printStackTrace();
        }
        try {
            servers = (HashMap<String, DiscApplicationServer>) engine.getFileUtils().loadObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Servers cant load!!", true);
            e.printStackTrace();
        }

        try {
            users = (HashMap<String, DiscApplicationUser>) engine.getFileUtils().loadObject(engine.getFileUtils().getHome() + "/bot/utilize/users.users");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Users cant load!!", true);
            e.printStackTrace();
        }

        if (servers == null) {
            engine.getUtilityBase().printOutput("!!Recreate Servers data!!", true);
            servers = new HashMap<>();
        }
        if (users == null) {
            engine.getUtilityBase().printOutput("!!Recreate Users data!!", true);
            users = new HashMap<>();
        }
        if (engine.getDiscEngine().getVoteCmd().getPolls() == null) {
            engine.getUtilityBase().printOutput("!!Recreate Vote data!!", true);
            engine.getDiscEngine().getVoteCmd().setPolls(new ArrayList<Poll>());
        }

        engine.getUtilityBase().printOutput("~finished loading bot files", true);
    }

    public void saveAllBotFiles() {
        engine.getUtilityBase().printOutput("~safe all bot files!", true);
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().home + "/vote/votes.dat", engine.getDiscEngine().getVoteCmd().getPolls());
        } catch (Exception e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - Votes", false);
        }
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server", servers);
        } catch (Exception e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - servers", false);
        }
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().getHome() + "/bot/utilize/users.users", users);
        } catch (Exception e) {
            e.printStackTrace();
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - users", false);
        }
        engine.getUtilityBase().printOutput("~finished saving all bot files", true);
    }

    public JSONObject serverToJson(DiscApplicationServer server){
        JSONObject obj = new JSONObject();
        //    serverName: String,
        //    serverId: String,
        //    serverYtPlaylist: String,
        //    musicListenerId: String,
        //    certificationMessageId: String,
        //    certificationChannelId: String,
        //    welcomeMessageChannelId: String,
        //    welcomeText: String,
        //    memberCountStatsChannelId: String,
        //    setupDone: { tyoe: Boolean, default: false },
        //    roleIds: [String],
        //    defaultMemberRoleId: String,
        //    defaultTempGamerRoleId: String,
        //    autoChannelIds: [String],
        //    primeRoleId: String,
        //    vipRoleId: String
        obj.put("serverName", server.getServerName());
        obj.put("serverId",server.getServerID());
        obj.put("serverYtPlaylist",server.getServerYTPlaylist());
        obj.put("certificationMessageId",server.getCertificationMessageId());
        obj.put("certificationChannelId",server.getCertificationChannelId());
        obj.put("welcomeMessageChannelId",server.getWelcomeMessageChannel());
        obj.put("welcomeText",server.getWelcomeText());
        obj.put("memberCountStatsChannelId",server.getMemberCountCategoryId());
        obj.put("setupDone",server.isSetupDone());
        obj.put("defaultMemberRoleId",server.getDefaultMemberRoleId());
        obj.put("defaultTempGamerRoleId",server.getDefaultTempGamerRoleId());
        obj.put("primeRoleId",server.getPrimeRoleId());
        obj.put("vipRoleId",server.getVipRoleId());
        obj.put("roleIds",getArrayFromArray(server.getDefaultRoles()));
        obj.put("autoChannelIds",getArrayFromArray(server.getAutoChannels()));
        return obj;
    }

    private JSONArray getArrayFromArray(ArrayList<String> ar){
        JSONArray arr = new JSONArray();
        for (String s:ar) {
            arr.add(s);
        }
        return arr;
    }

    public HashMap<String, DiscApplicationServer> getServers() {
        return servers;
    }

    public HashMap<String, DiscApplicationUser> getUsers() {
        return users;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public HashMap<String, Dungeon> getDungeons() {
        return dungeons;
    }

    public ArrayList<DiscCmdBait.Bait> getBaits() {
        return baits;
    }
}
