package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscCmdMusic implements DiscCommand {

    HashMap<String, String> channelMusicBots = new HashMap<>();

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if(event.getMember().getVoiceState().getChannel() == null){
            event.getChannel().sendMessage(new EmbedBuilder().setDescription("You are not in a valid Voice Channel!").setColor(Color.RED).build()).queue();
            return;
        }
        if(engine.getProperties().botSlaves.size()>=1){
            String vcId = event.getMember().getVoiceState().getChannel().getId();
            String botUrl = "";
            if(channelMusicBots.containsKey(vcId)){
                botUrl = channelMusicBots.get(vcId);
            } else {
                Object[] arr = channelMusicBots.values().toArray();
                for (String ss:engine.getProperties().botSlaves) {
                    boolean isFree = true;
                    for (Object o:arr) {
                        String s = (String) o;
                        if(s.equals(ss)){
                            isFree = false;
                        }
                    }
                    if(isFree){
                        botUrl = ss;
                        break;
                    }
                }
                if(botUrl.equals("")){
                    JSONObject o = new JSONObject();
                    o.put("guild", event.getGuild().getId());
                    Object[] array = channelMusicBots.keySet().toArray();
                    for (Object ob:array) {
                        String s = channelMusicBots.get((String) ob);
                        String res = engine.getNetworkManager().post(s + "/state", o.toJSONString());
                        JSONObject obj = engine.getFileUtils().convertStringToJson(res);
                        String ev = (String) obj.get("response");
                        if(ev.equals("true")){
                        } else if (ev.equals("false")){
                            channelMusicBots.remove((String) ob);
                            botUrl = s;
                        }
                    }
                    if(botUrl.equals("")){
                        event.getChannel().sendMessage(new EmbedBuilder().setDescription("There is no Music bot available").setColor(Color.RED).build()).queue();
                        return;
                    }
                }
            }

            String response = "";
            JSONObject r = new JSONObject();
            r.put("status", "200");
            JSONObject data = new JSONObject();
            data.put("guild", event.getGuild().getId());
            data.put("member", event.getMember().getId());
            response = argsToString(args);
            data.put("inst", response);
            r.put("data", data);
            channelMusicBots.put(vcId, botUrl);
            JSONObject req = engine.getFileUtils().convertStringToJson(engine.getNetworkManager().post(botUrl + "/api", r.toJSONString()));
            if(req == null){
                event.getChannel().sendMessage(new EmbedBuilder().setDescription("The Music Bot slave throws an error").setColor(Color.RED).build()).queue();
                return;
            }
            if(((String) req.get("status")).equals("200")){
                event.getChannel().sendMessage(new EmbedBuilder().setDescription((String) req.get("response")).setColor(Color.GREEN).build()).queue();
            } else if(((String) req.get("status")).equals("400")){
                event.getChannel().sendMessage(new EmbedBuilder().setDescription((String) req.get("response")).setColor(Color.RED).build()).queue();
            }
        }
    }

    private String argsToString(String[] ar){
        String s = "";
        for (String ss:ar) {
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
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
