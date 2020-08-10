package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.monsters.Monster;
import botApplication.discApplication.librarys.job.UserJob;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;

public class DiscCmdAdmin implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return user.isAdmin();
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        switch (args[0]) {
            case "reset":
                for (Object u:engine.getDiscEngine().getFilesHandler().getUsers().values().toArray()) {
                    DiscApplicationUser usr = (DiscApplicationUser) u;
                    usr.getMonsters().clear();
                    usr.setCoins(usr.getCoins() + 20);
                    usr.setLastWorkTime(null);
                }
                engine.getDiscEngine().getTextUtils().sendSucces("Done!", event.getChannel());
                break;

            case "user":
            case "usr":
                DiscApplicationUser rUser;
                try {
                    rUser = engine.getDiscEngine().getFilesHandler().getUserById(args[1]);
                } catch (Exception e) {
                    engine.getDiscEngine().getTextUtils().sendError("User not found!", event.getChannel(), false);
                    return;
                }
                switch (args[2]) {
                    case "mod":
                        switch (args[3]) {
                            case "coins":
                                rUser.setCoins(Long.valueOf(args[4]));
                                break;

                            case "jobxp":
                                rUser.getUserJob().setJobXp(Integer.valueOf(args[4]));
                                break;

                            case "joblevel":
                            case "joblvl":
                                rUser.getUserJob().setJobLevel(Integer.valueOf(args[4]));
                                break;

                            case "jobpos":
                                UserJob.JobRank r = rUser.getUserJob().stringToJobRank(args[4]);
                                if (r == null) {
                                    engine.getDiscEngine().getTextUtils().sendError("Rank invalid", event.getChannel(), false);
                                    return;
                                }
                                rUser.getUserJob().setJobRank(r);
                                break;

                            case "monster":
                                Monster rM = null;
                                if (args[4].startsWith("id:")) {
                                    String m = args[4].substring(3);
                                    for (Monster mo : engine.getDiscEngine().getFilesHandler().getMonsters()) {
                                        if (mo.getItemName().equals(m)) {
                                            rM = mo.clone();
                                        }
                                    }
                                } else {
                                    rM = rUser.getMonsters().get(Integer.parseInt(args[4]) -1);
                                }

                                if (rM == null) {
                                    engine.getDiscEngine().getTextUtils().sendError("invalid monster", event.getChannel(), false);
                                    return;
                                }
                                switch (args[5]) {
                                    case "delete":
                                        rUser.getMonsters().remove(rM);
                                        break;

                                    case "add":
                                        rUser.getMonsters().add(rM);
                                        break;

                                    case "mod":
                                        switch (args[6]) {
                                            case "lvl":
                                                rM.setLevel(Integer.parseInt(args[7]));
                                                rM.isEvolve(engine, rUser);
                                                break;

                                            default:
                                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                                break;
                                        }
                                        break;

                                    default:
                                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                        break;
                                }
                                break;

                            default:
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                break;
                        }
                        break;

                    case "show":
                        switch (args[3]) {
                            case "coins":
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(String.valueOf(rUser.getCoins()), event.getChannel(), "Info", Color.MAGENTA);
                                break;

                            case "jobxp":
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(String.valueOf(rUser.getUserJob().getJobXp()), event.getChannel(), "Info", Color.MAGENTA);
                                break;

                            case "joblevel":
                            case "joblvl":
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(String.valueOf(rUser.getUserJob().getJobLevel()), event.getChannel(), "Info", Color.MAGENTA);
                                break;

                            case "jobpos":
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(String.valueOf(rUser.getUserJob().jobRankToString(rUser.getUserJob().getJobRank())), event.getChannel(), "Info", Color.MAGENTA);
                                break;

                            case "monsters":
                                String msgg = "";
                                for (int i = 0; i < rUser.getMonsters().size(); i++) {
                                    Monster m = user.getMonsters().get(i);
                                    msgg += "[" + (i + 1) + "] " + m.getItemName() + "\n";
                                }
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(msgg, event.getChannel(), "Info", Color.MAGENTA);
                                break;

                            default:
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                break;

                        }
                        break;
                    default:
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                        break;
                }
                break;
            default:
                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                break;
        }
        engine.getDiscEngine().getTextUtils().sendSucces("Operation done!", event.getChannel());
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
        return "usr <usr> show/mod [coins/jobxp/joblvl/jobpos]";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
