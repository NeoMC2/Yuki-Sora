package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;

public class DiscCmdJob implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("list")) {
                JSONObject jbs = engine.getDiscEngine().getApiManager().getJobs();
                JSONArray jbss = (JSONArray) jbs.get("data");
                String jbS = "";
                for (Object o : jbss) {
                    JSONObject ob = (JSONObject) o;
                    jbS += "Work as " + ob.get("doing") + " at " + ob.get("jobName") + " [" + ob.get("shortName") + "]\n";
                }
                engine.getDiscEngine().getTextUtils().sendSucces(jbS, event.getChannel());
                return;
            }
            switch (args[0].toLowerCase()) {
                case "take":
                    JSONObject jbs = engine.getDiscEngine().getApiManager().getJobs();
                    JSONArray jbss = (JSONArray) jbs.get("data");
                    String jbS = "Select one of the following jobs\n\n";
                    for (int i = 0; i < jbss.size(); i++) {
                        JSONObject ob = (JSONObject) jbss.get(i);
                        jbS += "[" + i + "] Work as " + ob.get("doing") + " at " + ob.get("jobName") + "[" + ob.get("shortName") + "]\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(jbS, event.getChannel(), "Jobs", Color.blue);

                    Response r = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                            int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                            JSONObject o = (JSONObject) jbss.get(id);
                            String idd = (String) o.get("_id");
                            engine.getDiscEngine().getApiManager().giveUserAJob(respondingEvent.getAuthor().getId(), idd, "trainee");
                            engine.getDiscEngine().getTextUtils().sendSucces("You work as " + o.get("doing") + " at " + o.get("jobName"), respondingEvent.getChannel());
                        }
                    };
                    r.discChannelId = event.getChannel().getId();
                    r.discGuildId = event.getGuild().getId();
                    r.discUserId = event.getAuthor().getId();
                    engine.getResponseHandler().makeResponse(r);
                    break;

                case "work":
                    JSONObject workRes = engine.getDiscEngine().getApiManager().work(user.getUserId());
                    if (((Long) workRes.get("status")) == 200) {
                        engine.getDiscEngine().getTextUtils().sendSucces("You've got " + (workRes.get("data") + " weboos"), event.getChannel());
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError((String) workRes.get("message"), event.getChannel(), true);
                    }
                    break;

                case "info":
                    JSONObject infRes = engine.getDiscEngine().getApiManager().getUserJobAndJobFromUser(event.getAuthor().getId());
                    if (((Long) infRes.get("status")) == 200) {
                        JSONObject o = (JSONObject) infRes.get("job");
                        JSONObject ujb = (JSONObject) infRes.get("uJob");

                        int earning = 0;
                        String pos = (String) ujb.get("jobPosition");
                        switch (pos.toLowerCase()) {
                            case "trainee":
                                earning = Math.toIntExact((Long) o.get("earningTrainee"));
                                break;

                            case "coworker":
                                earning = Math.toIntExact((Long) o.get("earningCoworker"));
                                break;

                            case "headofdepartment":
                                earning = Math.toIntExact((Long) o.get("earningHeadOfDepartment"));
                                break;

                            case "manager":
                                earning = Math.toIntExact((Long) o.get("earningManager"));
                                break;
                        }

                        engine.getDiscEngine().getTextUtils().sendSucces("You work as " + o.get("doing") + " at " + o.get("jobName") + " [" + o.get("shortName") + "]. You are " + ujb.get("jobPosition") + " and earn " + earning + ". You have " + ujb.get("jobXP") + " xp and " + ujb.get("jobLevel") + " level! You are at " + (ujb.get("jobStreak") + ":fire:") , event.getChannel());
                    } else {

                    }
                    break;

                case "quit":
                    engine.getDiscEngine().getApiManager().removeUserAJob(user.getUserId());
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.work.success.quitJob", user.getLang(), null), event.getChannel());
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
            }
        }
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.job.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
