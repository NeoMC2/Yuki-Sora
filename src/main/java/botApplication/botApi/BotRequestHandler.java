package botApplication.botApi;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import java.awt.*;

public class BotRequestHandler {

    private final Engine engine;

    public BotRequestHandler(Engine engine) {
        this.engine = engine;
    }

    public JSONObject handle(JSONObject command) {
        String com = (String) command.get("instruction");
        JSONObject data = (JSONObject) command.get("data");
        switch (com){
            case "discconnect":{
                PrivateChannel pc = engine.getDiscEngine().getBotJDA().openPrivateChannelById((String) data.get("user")).complete();
                DiscApplicationUser user = DiscUtilityBase.lookForUserById(engine.getDiscEngine().getBotJDA().retrieveUserById((String) data.get("user")).complete(), engine);
                if(user == null){
                    return responseAnswer(null, "user not found", 400);
                }
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.BLUE).setAuthor("Mindcollaps.de connection authentication").setDescription("Token:\n> " + data.get("token"));
                pc.sendMessage(eb.build()).queue();
                return responseAnswer(null, "send message", 200);
            }
            case "giverole": {
                String user = (String) data.get("user");
                String role = (String) data.get("role");
                User usr = engine.getDiscEngine().getBotJDA().getUserById(user);

                if(usr == null)
                    return responseAnswer(null, "user not found", 400);

                switch (role){
                    case "vip":
                        for (Guild g:engine.getDiscEngine().getBotJDA().getMutualGuilds(usr)) {
                            DiscApplicationServer s = DiscUtilityBase.lookForServer(g, engine);
                            try {
                                g.addRoleToMember(usr.getId(), g.getRoleById(s.getVipRoleId())).queue();
                            } catch (Exception e){
                                return responseAnswer(null, "error", 400);
                            }
                        }
                        break;

                    case "prime":
                        for (Guild g:engine.getDiscEngine().getBotJDA().getMutualGuilds(usr)) {
                            DiscApplicationServer s = DiscUtilityBase.lookForServer(g, engine);
                            try {
                                g.addRoleToMember(usr.getId(), g.getRoleById(s.getPrimeRoleId())).queue();
                            } catch (Exception e){
                                return responseAnswer(null, "error", 400);
                            }
                        }
                        break;
                }
                return responseAnswer(null, "gave role", 200);
            }
            case "sendmsg": {
                String title = (String) data.get("title");
                String color = (String) data.get("color");
                String msg = (String) data.get("msg");
                String user = (String) data.get("user");

                Color c = null;
                try {
                    c = new Color(Integer.parseInt(color));
                } catch (Exception e){
                }

                if(c == null)
                    c = Color.white;

                User usr = engine.getDiscEngine().getBotJDA().getUserById(user);
                if(usr == null)
                    return responseAnswer(null, "user not found", 400);

                PrivateChannel pc = engine.getDiscEngine().getBotJDA().openPrivateChannelById(usr.getId()).complete();
                engine.getDiscEngine().getTextUtils().sendCustomMessage(msg, pc, title, c);
                break;
            }
        }
        return null;
    }

    public JSONObject responseAnswer(JSONObject dataj, String message, int status){
        JSONObject res = new JSONObject();
        res.put("status", status);
        if(message != null)
        res.put("message", message);
        if(dataj != null)
        res.put("data", dataj);

        return res;
    }
}
