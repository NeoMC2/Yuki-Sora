package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.HashMap;

public class DiscCmdMusic implements DiscCommand {

    HashMap<String, String> channelMusicBots = new HashMap<>();

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        event.getChannel().sendMessage(disputeCommand(event.getGuild(), event.getMember(), event.getChannel(), engine, args)).queue();
    }

    private String argsToString(String[] ar) {
        String s = "";
        for (String ss : ar) {
            s += ss + " ";
        }
        return s;
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        event.getHook().sendMessageEmbeds((disputeCommand(event.getGuild(), event.getMember(), event.getChannel(), engine, args))).queue();
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @NotNull
    @Override
    public CommandData getCommand() {
        return new CommandData(getInvoke(), "Play music").addSubcommands(
                new SubcommandData("play", "Play a song").addOption(OptionType.STRING, "url", "YT search / YT URL / Spotify URL", true),
                new SubcommandData("skip", "Skip a song"),
                new SubcommandData("add", "Add a song to playlist").addOption(OptionType.STRING, "url", "YT search / YT URL / Spotify URL", true),
                new SubcommandData("shuffle", "Shuffle a playlist"),
                new SubcommandData("playlist", "Show the playlist").addOption(OptionType.INTEGER, "site", "Playlist site", true),
                new SubcommandData("stop", "Stops bot from playing"),
                new SubcommandData("info", "Shows information about the currently playing song"),
                new SubcommandData("repeat", "Repeats the currently playing song, or stops the reputation")
        );
    }

    @Override
    public String getInvoke() {
        return "m";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private MessageEmbed disputeCommand(Guild g, Member member, MessageChannel channel, Engine engine, String[] args){
        if (member.getVoiceState().getChannel() == null) {
            return new EmbedBuilder().setDescription("You are not in a valid Voice Channel!").setColor(Color.RED).build();
        }
        if (engine.getProperties().botSlaves.size() >= 1) {
            String vcId = member.getVoiceState().getChannel().getId();
            String botUrl = "";
            if (channelMusicBots.containsKey(vcId)) {
                botUrl = channelMusicBots.get(vcId);
            } else {
                Object[] arr = channelMusicBots.values().toArray();
                for (String ss : engine.getProperties().botSlaves) {
                    boolean isFree = true;
                    for (Object o : arr) {
                        String s = (String) o;
                        if (s.equals(ss)) {
                            isFree = false;
                        }
                    }
                    if (isFree) {
                        botUrl = ss;
                        break;
                    }
                }
                if (botUrl.equals("")) {
                    JSONObject o = new JSONObject();
                    o.put("guild", g.getId());
                    Object[] array = channelMusicBots.keySet().toArray();
                    for (Object ob : array) {
                        String s = channelMusicBots.get(ob);
                        String res = engine.getNetworkManager().post(s + "/state", o.toJSONString(), null);
                        JSONObject obj = engine.getFileUtils().convertStringToJson(res);
                        String ev = (String) obj.get("response");
                        if (ev.equals("true")) {
                        } else if (ev.equals("false")) {
                            channelMusicBots.remove(ob);
                            botUrl = s;
                        }
                    }
                    if (botUrl.equals("")) {
                        return new EmbedBuilder().setDescription("There is no Music bot available").setColor(Color.RED).build();
                    }
                }
            }
            return makeSlaveRequest(g, member, vcId, botUrl, args, engine);
        } else {
            return new EmbedBuilder().setDescription("There is no Music bot available").setColor(Color.RED).build();
        }
    }

    private MessageEmbed makeSlaveRequest(Guild g, Member m, String vcId, String botUrl, String[] args, Engine engine) {
        String response = "";
        JSONObject r = new JSONObject();
        r.put("status", "200");
        JSONObject data = new JSONObject();
        if (g != null)
            data.put("guild", g.getId());
        if (m != null)
            data.put("member", m.getId());
        if (args != null)
            response = argsToString(args);
        data.put("inst", response);
        r.put("data", data);
        if (g != null && m != null && vcId != null && botUrl != null)
            channelMusicBots.put(vcId, botUrl);
        JSONObject req = engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(botUrl + "/api", r.toJSONString(), null));
        if (g != null && m != null && vcId != null && botUrl != null)
            if (req == null) {
                return new EmbedBuilder().setDescription("The Music Bot slave throws an error").setColor(Color.RED).build();
            }
        if (g != null && m != null && vcId != null && botUrl != null)
            if (req.get("status").equals("200")) {
                return new EmbedBuilder().setDescription((String) req.get("response")).setColor(Color.GREEN).build();
            } else if (req.get("status").equals("400")) {
                return new EmbedBuilder().setDescription((String) req.get("response")).setColor(Color.RED).build();
            }

        return new EmbedBuilder().setDescription(((String) req.get("response")).replace("\\\"","\"")).setColor(Color.RED).setAuthor("An error occurred!").build();
    }
}
