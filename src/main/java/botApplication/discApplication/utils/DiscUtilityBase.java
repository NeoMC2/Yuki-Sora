package botApplication.discApplication.utils;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import core.Engine;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class DiscUtilityBase {

    public static boolean userHasGuildAdminPermission(Member member, Guild guild, TextChannel textChannel, Engine engine) {
        boolean hasPermission = false;
        for (int i = 0; member.getRoles().size() > i; i++) {
            for (int a = 0; member.getRoles().get(i).getPermissions().toArray().length > a; a++) {
                if (member.getRoles().get(i).getPermissions().toArray()[a] == Permission.ADMINISTRATOR) {
                    hasPermission = true;
                    break;
                }
            }
        }
        if (hasPermission) {
            return true;
        } else {
            engine.getDiscEngine().getTextUtils().sendError("Du hast keine Berechtigung um diesen Command auzuführen! Dafür musst du Admin auf diesem Guild sein!", textChannel, engine.getProperties().middleTime, true);
            return false;
        }
    }

    public static DiscApplicationUser lookForUserById(User discuser, Engine engine) {
        DiscApplicationUser user = null;

        try {
            user = engine.getDiscEngine().getFilesHandler().getUserById(discuser.getId());
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("![Ai Engine] " + discuser.getId() + " User not found!", true);
        }

        if (user == null) {
            try {
                user = engine.getDiscEngine().getFilesHandler().createNewUser(discuser, DiscCertificationLevel.Temp);
            } catch (Exception e) {
                System.out.println("Fatal error in ServerMessageListener.sendOwnCommand()---user cant load!!!");
            }
        }
        return user;
    }

    public static DiscApplicationServer lookForServer(Guild guild, Engine engine) {
        DiscApplicationServer server = null;

        try {
            server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("![Ai Engine] " + guild.getId() + " Server not found!", true);
        }

        if (server == null) {
            JSONObject req = engine.getDiscEngine().getApiManager().getServerById(guild.getId());
            if(((Long) req.get("status")) == 200){
                DiscApplicationServer ser = new DiscApplicationServer(guild);
                ser.generateFromJSON((JSONObject) req.get("data"));
                ser.setEdit(false);
                engine.getDiscEngine().getFilesHandler().getServers().put(ser.getServerID(), ser);
                server = ser;
            }

            if(server == null)
            try {
                server = engine.getDiscEngine().getFilesHandler().createNewServer(guild);
            } catch (Exception e) {
                System.out.println("Fatal error in ServerMessageListener.sendOwnCommand()---server cant load!!!");
            }
        }
        return server;
    }

    public static String getMonsterListFromUserMonsters(Engine engine, JSONArray mnsters){
        JSONArray roots = (JSONArray) ((JSONObject) engine.getDiscEngine().getApiManager().getMonsters()).get("data");
        ArrayList<JSONObject> monsters = new ArrayList<>();
        for (Object o:mnsters) {
            JSONObject obj = (JSONObject) o;
            for (Object ob:roots) {
                JSONObject obj1 = (JSONObject) ob;
                if(((String) obj.get("rootMonster")).equals((String)obj1.get("_id"))){
                    monsters.add(obj1);
                }
            }
        }
        String s = "";
        for (int i = 0; i < monsters.size(); i++) {
            s += "[" + i + "] " + ((String) monsters.get(i).get("name") + "\n");
        }
        return s;
    }

    public static String getAttacksListFromUserMonster(Engine engine, JSONArray attacks){
        String s = "";
        for (int i = 0; i < attacks.size(); i++) {
            JSONObject at = (JSONObject) attacks.get(i);
            s += at.get("[" + i + "] " + at.get("attackName") + " dmg: " + at.get("baseDmg") + " type: " + at.get("attackType") + " status effect: " + at.get("statusEffect") + "\n");
        }
        return s;
    }
}
