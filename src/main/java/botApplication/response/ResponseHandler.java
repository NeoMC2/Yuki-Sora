package botApplication.response;

//import com.pengrad.telegrambot.model.Update;

import core.Engine;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ResponseHandler {

    private final String consMsgDef = "[Response Handler]";
    private final Engine engine;
    private final ArrayList<Response> responses = new ArrayList<>();

    public ResponseHandler(Engine engine) {
        this.engine = engine;
    }

    public void makeResponse(Response response) {
        responses.add(response);
    }

    /*
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
     */

    public void startUpdateThread() {
        engine.getUtilityBase().printOutput("[Response Handler] starting update Thread", true);
        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                try {
                    while (responses.iterator().hasNext()) {
                        Response r = responses.iterator().next();
                        Instant now = new Date().toInstant();
                        now.minus(10, ChronoUnit.MINUTES);
                        if (now.isAfter(r.creationTime.toInstant())) {
                            responses.iterator().remove();
                        }
                    }
                } catch (Exception e) {
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                    engine.getUtilityBase().printOutput("[Response Handler] updater had an error!", true);
                }
            }
        };
        t.schedule(tt, 10 * 10 * 60, 10 * 10 * 60 * 5);
    }

    public boolean lookForResponse(GuildMessageReceivedEvent update) {
        final ArrayList<Response> r = responses;
        Response re = null;
        try {
            for (Response res : r) {
                if (res.responseTyp == Response.ResponseTyp.Discord)
                    if (update.getAuthor().getId().equals(res.discUserId)) {
                        if (update.getChannel().getId().equals(res.discChannelId)) {
                            re = res;
                            engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                            responses.remove(res);
                            res.onGuildMessage(update);
                            return true;
                        }
                    }
            }
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Response called error!!!", true);
            if (engine.getProperties().debug)
                e.printStackTrace();

            if (re != null) {
                try {
                    re.onError(e);
                } catch (Exception ee) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Response error called error!!!", true);
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                }
            }
            return false;
        }
        return false;
    }

    public boolean lookForResponse(PrivateMessageReceivedEvent update) {
        final ArrayList<Response> r = responses;
        Response re = null;
        try {
            for (Response res : r) {
                if (res.responseTyp == Response.ResponseTyp.Discord)
                    if (update.getAuthor().getId().equals(res.discUserId)) {
                        if (update.getChannel().getId().equals(res.discChannelId)) {
                            re = res;
                            engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                            responses.remove(res);
                            res.onPrivateMessage(update);
                            return true;
                        }
                    }
            }
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Response called error!!!", true);
            if (engine.getProperties().debug)
                e.printStackTrace();

            if (re != null) {
                try {
                    re.onError(e);
                } catch (Exception ee) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Response error called error!!!", true);
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                }
            }
            return false;
        }
        return false;
    }

    public boolean lookForResponse(GuildMessageReactionAddEvent update) {
        final ArrayList<Response> r = responses;
        Response re = null;
        try {
            for (Response res : r) {
                if (update.getMember().getId().equals(res.discUserId)) {
                    if (update.getChannel().getId().equals(res.discChannelId)) {
                        re = res;
                        engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                        responses.remove(res);
                        res.onGuildEmote(update);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Response called error!!!", true);
            if (engine.getProperties().debug)
                e.printStackTrace();

            if (re != null) {
                try {
                    re.onError(e);
                } catch (Exception ee) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Response error called error!!!", true);
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                }
            }
            return false;
        }
        return false;
    }

    public boolean lookForResponse(PrivateMessageReactionAddEvent update) {
        final ArrayList<Response> r = responses;
        Response re = null;
        try {
            for (Response res : r) {
                if (update.getUser().getId().equals(res.discUserId)) {
                    if (update.getChannel().getId().equals(res.discChannelId)) {
                        re = res;
                        engine.getUtilityBase().printOutput(consMsgDef + " !Found response -> Respond!", true);
                        responses.remove(res);
                        res.onPrivateEmote(update);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Response called error!!!", true);
            if (engine.getProperties().debug)
                e.printStackTrace();

            if (re != null) {
                try {
                    re.onError(e);
                } catch (Exception ee) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Response error called error!!!", true);
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                }
            }
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
