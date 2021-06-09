package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
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
        event.getChannel().sendMessage(perform(user, engine, args, event.getChannel(), event.getGuild(), event.getAuthor())).queue();
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        event.getChannel().sendMessage(perform(user, engine, args, event.getChannel(), null, event.getAuthor())).queue();
    }

    @Override
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args[0].equals("take"))
            event.getChannel().sendMessage(perform(user, engine, args, event.getChannel(), event.getGuild(), event.getUser())).queue();
        else
            event.getHook().sendMessageEmbeds(perform(user, engine, args, event.getChannel(), event.getGuild(), event.getUser())).queue();
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.job.help", user.getLang(), null);
    }

    @NotNull
    @Override
    public CommandData getCommand() {
        return new CommandData(getInvoke(), "Work and manage your job").addSubcommands(
                new SubcommandData("work", "Go to work"),
                new SubcommandData("take", "Take a job"),
                new SubcommandData("info", "Shows information about your job")
        );
    }

    @Override
    public String getInvoke() {
        return "job";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private MessageEmbed perform(DiscApplicationUser user, Engine engine, String[] args, MessageChannel channel, Guild g, User usr) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("list")) {
                JSONObject jbs = engine.getDiscEngine().getApiManager().getJobs();
                JSONArray jbss = (JSONArray) jbs.get("data");
                String jbS = "";
                for (Object o : jbss) {
                    JSONObject ob = (JSONObject) o;
                    jbS += "Work as " + ob.get("doing") + " at " + ob.get("jobName") + " [" + ob.get("shortName") + "]\n";
                }
                return new EmbedBuilder().setColor(Color.GREEN).setDescription(jbS).build();
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

                    Response r = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                            stepTwo(engine, respondingEvent.getAuthor(), respondingEvent.getMessage().getContentRaw(), jbss, respondingEvent.getChannel());
                        }

                        @Override
                        public void onPrivateMessage(PrivateMessageReceivedEvent respondingEvent) {
                            stepTwo(engine, respondingEvent.getAuthor(), respondingEvent.getMessage().getContentRaw(), jbss, respondingEvent.getChannel());
                        }
                    };
                    r.discChannelId = channel.getId();
                    if (g != null)
                        r.discGuildId = g.getId();
                    r.discUserId = usr.getId();
                    engine.getResponseHandler().makeResponse(r);
                    return new EmbedBuilder().setColor(Color.BLUE).setDescription(jbS).setAuthor("Jobs").build();

                case "work":
                    JSONObject workRes = engine.getDiscEngine().getApiManager().work(user.getUserId());
                    if (((Long) workRes.get("status")) == 200) {
                        return new EmbedBuilder().setColor(Color.GREEN).setDescription("You've got " + (workRes.get("data") + " weboos")).build();
                    } else {
                        return new EmbedBuilder().setColor(Color.RED).setDescription((String) workRes.get("message")).build();
                    }

                case "info":
                    JSONObject infRes = engine.getDiscEngine().getApiManager().getUserJobAndJobFromUser(usr.getId());
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

                        return new EmbedBuilder().setColor(Color.GREEN).setDescription("You work as " + o.get("doing") + " at " + o.get("jobName") + " [" + o.get("shortName") + "]. You are " + ujb.get("jobPosition") + " and earn " + earning + ". You have " + ujb.get("jobXP") + " xp and " + ujb.get("jobLevel") + " level! You are at " + (ujb.get("jobStreak") + ":fire:")).build();
                    } else {

                    }
                    break;

                case "quit":
                    engine.getDiscEngine().getApiManager().removeUserAJob(user.getUserId());
                    return new EmbedBuilder().setColor(Color.GREEN).setDescription(engine.lang("cmd.work.success.quitJob", user.getLang(), null)).build();

                default:
                    return new EmbedBuilder().setColor(Color.GREEN).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
            }
        }
        return new EmbedBuilder().setColor(Color.GREEN).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
    }

    private void stepTwo(Engine engine, User user, String message, JSONArray jbss, MessageChannel channel) {
        int id = Integer.parseInt(message);
        JSONObject o = (JSONObject) jbss.get(id);
        String idd = (String) o.get("_id");
        engine.getDiscEngine().getApiManager().giveUserAJob(user.getId(), idd, "trainee");
        engine.getDiscEngine().getTextUtils().sendSucces("You work as " + o.get("doing") + " at " + o.get("jobName"), channel);
    }
}
