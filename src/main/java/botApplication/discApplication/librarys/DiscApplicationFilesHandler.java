package botApplication.discApplication.librarys;

import botApplication.discApplication.commands.DiscCmdVote;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import botApplication.discApplication.librarys.poll.Poll;
import core.Engine;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscApplicationFilesHandler {

    private HashMap<String, DiscApplicationServer> servers = new HashMap<>();
    private HashMap<String, DiscApplicationUser> users = new HashMap<>();

    private Engine engine;

    public DiscApplicationFilesHandler(Engine engine) {
        this.engine = engine;
    }

    public DiscApplicationServer getServerById(String id) {
        DiscApplicationServer server = null;
        if(servers.containsKey(id)){
            server = servers.get(id);
        }
        return server;
    }

    public DiscApplicationUser getUserById(String id) throws Exception {
        DiscApplicationUser user = null;
        if(users.containsKey(id)){
            user = users.get(id);
        }
        return user;
    }

    public DiscApplicationServer createNewServer(Guild guild) throws Exception{
        if(servers.containsKey(guild.getId())){
            engine.getUtilityBase().printOutput("Override Server! Id: " + guild.getId() + " name: " + guild.getName(), true);
            servers.remove(guild.getId());
        }
        DiscApplicationServer server = new DiscApplicationServer(guild);
        servers.put(guild.getId(), server);
        return server;
    }

    public DiscApplicationUser createNewUser(User user, DiscCertificationLevel discCertificationLevel) {
        if(users.containsKey(user.getId())){
            engine.getUtilityBase().printOutput("Override User! Id: " + user.getId() + " name: " + user.getName(), true);
            users.remove(user.getId());
        }
        DiscApplicationUser botUser = new DiscApplicationUser(user, discCertificationLevel);
        users.put(user.getId(), botUser);
        return botUser;
    }

    public void loadAllBotFiles(){
        engine.getUtilityBase().printOutput("~load all bot files!",true);

        try {
            engine.getDiscEngine().getVoteCmd().setPolls((ArrayList<Poll>) engine.getFileUtils().loadObject(engine.getFileUtils().home + "/vote/votes.dat"));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Votes cant load!!",true);
        }
        try {
            servers = (HashMap<String, DiscApplicationServer>) engine.getFileUtils().loadObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Servers cant load!!",true);
        }

        try {
            users = (HashMap<String, DiscApplicationUser>) engine.getFileUtils().loadObject(engine.getFileUtils().getHome() + "/bot/utilize/users.users");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Users cant load!!", true);
            System.out.println("!!Users cant load!!");
        }

        if(servers==null){
            engine.getUtilityBase().printOutput("!!Recreate Servers data!!",true);
            servers = new HashMap<>();
        }
        if(users==null){
            engine.getUtilityBase().printOutput("!!Recreate Users data!!",true);
            users = new HashMap<>();
        }
        if(engine.getDiscEngine().getVoteCmd().getPolls()==null){
            engine.getUtilityBase().printOutput("!!Recreate Vote data!!",true);
            engine.getDiscEngine().getVoteCmd().setPolls(new ArrayList<Poll>());
        }

        engine.getUtilityBase().printOutput("~finished loading bot files",true);
    }

    public void saveAllBotFiles(){
        engine.getUtilityBase().printOutput("~safe all bot files!",true);
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().home + "/vote/votes.dat", engine.getDiscEngine().getVoteCmd().getPolls());
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - Votes", false);
        }
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server", servers);
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - servers", false);
        }
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().getHome() + "/bot/utilize/users.users", users);
        } catch (Exception e) {
            e.printStackTrace();
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO - users", false);
        }
        engine.getUtilityBase().printOutput("~finished saving all bot files",true);
    }

    public HashMap<String, DiscApplicationServer> getServers() {
        return servers;
    }

    public HashMap<String, DiscApplicationUser> getUsers() {
        return users;
    }
}
