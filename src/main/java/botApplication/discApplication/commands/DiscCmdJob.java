package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.job.Job;
import botApplication.discApplication.librarys.job.UserJob;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class DiscCmdJob implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length > 0) {
            if (args[0].toLowerCase().equals("list")) {
                //TODO: make this
                String msg = "";
                for (Job j : engine.getDiscEngine().getFilesHandler().getJobs()) {
                    msg += "[" + j.getShortName() + "] " + j.getJobName() + "\n";
                }
                engine.getDiscEngine().getTextUtils().sendSucces(msg, event.getChannel());
                return;
            }
            switch (args[0].toLowerCase()) {
                case "take":
                    //TODO: make this
                    for (Job j : engine.getDiscEngine().getFilesHandler().getJobs()) {
                        if (j.getShortName().toLowerCase().equals(args[1].toLowerCase())) {
                            UserJob uj = new UserJob();
                            uj.setJob(j);
                            user.setUserJob(uj);
                            user.getUserJob().setJobRank(UserJob.JobRank.Trainee);
                            engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.work.success.take", user.getLang(), new String[]{uj.getJobName(), uj.getDoing()}), event.getChannel());
                        }
                    }
                    break;

                case "work":
                    JSONObject workRes = engine.getDiscEngine().getApiManager().work(user.getUserId());
                    if(((Long) workRes.get("status")) == 200){
                        engine.getDiscEngine().getTextUtils().sendSucces("You got " + ((Long) workRes.get("data")), event.getChannel());
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError((String) workRes.get("message"), event.getChannel(), true);
                    }
                    break;

                case "info":
                    JSONObject jbs = engine.getDiscEngine().getApiManager().getJobs();
                    JSONArray jbss = (JSONArray) jbs.get("data");
                    String jbS = "";
                    for (Object o:jbss) {
                        JSONObject ob = (JSONObject) o;
                        jbS += "Work as" + (String) ob.get("doing") + " at " + (String) ob.get("jobName") + "[" + (String) ob.get("shortName") + "]\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendSucces(jbS, event.getChannel());
                    break;

                case "quit":
                    user.setUserJob(null);
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

    private String evolved(String msg, DiscApplicationUser user, Engine engine) {
        if (user.getUserJob().isLvlUp()) {
            msg += "\n\n" + engine.lang("cmd.work.info.levelUp", user.getLang(), new String[]{String.valueOf(user.getUserJob().getJobLevel())});
        }
        if (user.getUserJob().isPositionUp()) {
            msg += "\n\n" + engine.lang("cmd.work.info.positionUp", user.getLang(), new String[]{String.valueOf(user.getUserJob().jobRankToString(user.getUserJob().getJobRank()))});
        }
        return msg;
    }
}
