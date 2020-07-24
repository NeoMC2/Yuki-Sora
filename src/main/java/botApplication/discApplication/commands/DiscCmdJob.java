package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.job.Job;
import botApplication.discApplication.librarys.job.UserJob;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

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
            if (args[0].equals("list")) {
                String msg = "";
                for (Job j : engine.getDiscEngine().getFilesHandler().getJobs()) {
                    msg += "[" + j.getShortName() + "] " + j.getJobName() + "\n";
                }
                engine.getDiscEngine().getTextUtils().sendSucces(msg, event.getChannel());
                return;
            }
            switch (args[0]) {
                case "take":
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
                    if (user.getUserJob() == null) {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.work.error.noWork", user.getLang(), null), event.getChannel(), false);
                    } else {
                        if(user.getLastWorkTime() != null){
                            Instant fourHoursAgo = Instant.now().minus(Duration.ofHours(4));
                            Date dFourHoursAgo = Date.from(fourHoursAgo);
                            if(user.getLastWorkTime().after(dFourHoursAgo)){
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.job.error.workedAlready", user.getLang(), null), event.getChannel(), false);
                                return;
                            }
                        }
                        int earned = 0;
                        try {
                            earned = user.getUserJob().work();
                        } catch (Exception e) {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.work.error.noWork", user.getLang(), null), event.getChannel(), false);
                            return;
                        }
                        user.setLastWorkTime(new Date());
                        user.addCoins(earned);
                        String msg = engine.lang("cmd.work.info.worked", user.getLang(), new String[]{user.getUserJob().getDoing(), String.valueOf(earned)});
                        msg = evolved(msg, user, engine);
                        engine.getDiscEngine().getTextUtils().sendSucces(msg, event.getChannel());
                    }
                    break;

                case "info":
                    if (user.getUserJob() == null) {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.work.error.noWork", user.getLang(), null), event.getChannel(), false);
                    } else {
                        int earn = 0;

                        if (user.getUserJob().getJobRank() == UserJob.JobRank.Trainee) {
                            earn = user.getUserJob().getEarningTrainee();
                        } else if (user.getUserJob().getJobRank() == UserJob.JobRank.CoWorker) {
                            earn = user.getUserJob().getEarningCoWorker();
                        } else if (user.getUserJob().getJobRank() == UserJob.JobRank.HeadOfDepartment) {
                            earn = user.getUserJob().getEarningHeadOfDepartment();
                        } else if (user.getUserJob().getJobRank() == UserJob.JobRank.Manager) {
                            earn = user.getUserJob().getEarningManager();
                        }
                        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.work.info.info", user.getLang(), new String[]{user.getUserJob().getDoing(), user.getUserJob().jobRankToString(user.getUserJob().getJobRank()), user.getUserJob().getJobName(), String.valueOf(earn), String.valueOf(user.getUserJob().getJobXp()), String.valueOf(user.getUserJob().getJobLevel())}), event.getChannel());
                    }
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

    private String evolved(String msg, DiscApplicationUser user, Engine engine){
        if(user.getUserJob().isLvlUp()){
            msg += "\n\n" + engine.lang("cmd.work.info.levelUp", user.getLang(), new String[]{String.valueOf(user.getUserJob().getJobLevel())});
        }
        if(user.getUserJob().isPositionUp()){
            msg += "\n\n" + engine.lang("cmd.work.info.positionUp", user.getLang(), new String[]{String.valueOf(user.getUserJob().jobRankToString(user.getUserJob().getJobRank()))});
        }
        return msg;
    }
}
