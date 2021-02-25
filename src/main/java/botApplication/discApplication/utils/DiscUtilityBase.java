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
        if(discuser == null)
            return null;

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
            if(req == null)
                return null;
            if(req == null)
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
        JSONArray roots = (JSONArray) engine.getDiscEngine().getApiManager().getMonsters().get("data");
        ArrayList<JSONObject> monsters = new ArrayList<>();
        for (Object o:mnsters) {
            JSONObject obj = (JSONObject) o;
            for (Object ob:roots) {
                JSONObject obj1 = (JSONObject) ob;
                if(obj.get("rootMonster").equals(obj1.get("_id"))){
                    monsters.add(obj1);
                }
            }
        }
        String s = "";
        for (int i = 0; i < monsters.size(); i++) {
            s += "[" + i + "] " + (monsters.get(i).get("name") + "\n");
        }
        return s;
    }

    public static String getAttacksListFromAttackList(Engine engine, JSONArray attacks){
        String s = "";
        for (int i = 0; i < attacks.size(); i++) {
            JSONObject at = (JSONObject) attacks.get(i);
            s += "[" + i + "] " + getAttackInfo(at)+ "\n";
        }
        return s;
    }

    public static String getAttackInfo(JSONObject at){
        return at.get("attackName") + " dmg: " + at.get("baseDmg") + " type: " + at.get("attackType") + " status effect: " + at.get("statusEffect");
    }

    public static String getMonsterInfo(Engine engine, JSONObject mon){
        String s = "";
        JSONObject atts = engine.getDiscEngine().getApiManager().getAttacks();
        JSONObject root = null;
        JSONArray roots = (JSONArray) engine.getDiscEngine().getApiManager().getMonsters().get("data");
        for (Object ob:roots) {
            JSONObject obj1 = (JSONObject) ob;
            if(mon.get("rootMonster").equals(obj1.get("_id"))){
                root = obj1;
            }
        }

        JSONArray attacks = (JSONArray) atts.get("data");

        JSONObject a1 = null;
        JSONObject a2 = null;
        JSONObject a3 = null;
        JSONObject a4 = null;

        String a1S = (String) mon.get("a1");
        String a2S = (String) mon.get("a2");
        String a3S = (String) mon.get("a3");
        String a4S = (String) mon.get("a4");

        String a1T = "Not selected";
        String a2T = "Not selected";
        String a3T = "Not selected";
        String a4T = "Not selected";


        if(a1S != null){
            a1 = findAttack(a1S, attacks);
        }

        if(a2S != null){
            a2 = findAttack(a2S, attacks);
        }

        if(a3S != null){
            a3 = findAttack(a3S, attacks);
        }

        if(a4S != null){
            a4 = findAttack(a4S, attacks);
        }

        if(a1 != null){
            a1T = getAttackInfo(a1);
        }

        if(a2 != null){
            a2T = getAttackInfo(a2);
        }

        if(a3 != null){
            a3T = getAttackInfo(a3);
        }

        if(a4 != null){
            a4T = getAttackInfo(a4);
        }

        String attss = "";
        try {
            attss = getAttacksListFromAttackList(engine, (JSONArray) engine.getDiscEngine().getApiManager().getAttacksByUserMonster((String) mon.get("_id")).get("data"));
        } catch (Exception e){
        }


        s = root.get("name") + ", xp: " + getNumber(mon, "xp") + ", level: " + mon.get("level") + ", hp: " + getNumber(mon, "hp") + " [" + getNumber(mon, "maxHp") + "] dv: " + (mon.get("dv") + "\n\n**Selected Attacks**\na1: " + a1T + "\na2: " + a2T + "\na3: " + a3T + "\na4: " + a4T + "\n\n**Available Attacks**\n" + attss);
        return s;
    }

    private static int getNumber(JSONObject o, String r) {
        try {
            return Math.toIntExact((Long) o.get(r));
        } catch (Exception e) {
            return Math.toIntExact(Math.round((Double) o.get(r)));
        }
    }

    public static JSONObject findAttack(String id, JSONArray atts){
        for (Object o:atts) {
            JSONObject a = (JSONObject) o;
            if(id.equals(a.get("_id")))
                return a;
        }
        return null;
    }
}
