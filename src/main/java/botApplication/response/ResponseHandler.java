package botApplication.response;

import com.pengrad.telegrambot.model.Update;
import core.Engine;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Date;

public class ResponseHandler {

    private final String consMsgDef = "[Response Handler]";
    private final Engine engine;
    private final ArrayList<Response> responses = new ArrayList<>();

    public ResponseHandler(Engine engine) {
        this.engine = engine;
    }

    public void makeResponse(Response response) {
        /*
        response.creationTime = lookForCurrentTime();
        for (Response res: responses) {
            if(response.discUserId.equals(res.discUserId)){
                responses.remove(res);
                engine.getUtilityBase().printOutput(consMsgDef + " !User has already a response -> Override!", true);
            }
        }
         */
        responses.add(response);
    }

    public boolean lookForResponse(Update update) {
        try {
            for (Response res : responses) {
                if (res.creationTime + 2 < lookForCurrentTime()) {
                    responses.remove(res);
                    engine.getUtilityBase().printOutput(consMsgDef + " !Response is outdated -> Delete!", true);
                    continue;
                }
                if (update.message().from().id().equals(res.teleResponseUser.id())) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                    responses.remove(res);
                    res.respondTele(update);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean lookForResponse(GuildMessageReceivedEvent update) {
        final ArrayList<Response> r = responses;
        try {
            for (Response res : r) {
                /*
                if(res.creationTime+2<lookForCurrentTime()){
                    responses.remove(res);
                    engine.getUtilityBase().printOutput(consMsgDef + " !Response is outdated -> Delete!", true);
                    continue;
                }
                 */
                if (update.getAuthor().getId().equals(res.discUserId)) {
                    if(update.getChannel().getId().equals(res.discChannelId)){
                        engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                        responses.remove(res);
                        res.respondDisc(update);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Response called error!!!", true);
            if (engine.getProperties().debug)
                e.printStackTrace();
            return false;
        }
        return false;
    }

    private int lookForCurrentTime() {
        Date now = new Date();
        String hours = String.valueOf(now.getHours());
        String minutes = String.valueOf(now.getMinutes());
        return Integer.valueOf(hours + minutes);
    }
}
