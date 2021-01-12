package botApplication.botApi;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import jdk.nashorn.internal.parser.JSONParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.json.simple.JSONObject;
import utils.UtilityBase;

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
