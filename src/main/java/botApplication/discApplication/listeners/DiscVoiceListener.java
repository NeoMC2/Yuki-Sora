package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.autochan.AutoChannel;
import botApplication.discApplication.utils.DiscUtilityBase;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class DiscVoiceListener extends ListenerAdapter {

    public static ArrayList<AutoChannel> activeAutoChannels = new ArrayList<>();
    private final Engine engine;

    /* Feature deleted by member request
    private final HashMap<String, VoiceChannel> deafens = new HashMap<>();
    private final HashMap<String, ScheduledExecutorService> scheudlers = new HashMap<>();
     */

    public DiscVoiceListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (checkVc(event.getGuild(), event.getChannelJoined(), event.getEntity())) return;
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel vc = event.getChannelLeft();
        testActiveVC(vc);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (checkVc(event.getGuild(), event.getChannelJoined(), event.getEntity())) return;

        VoiceChannel vc = event.getChannelLeft();
        testActiveVC(vc);
    }

    private boolean checkVc(Guild guild, VoiceChannel channelJoined, Member entity) {
        DiscApplicationServer server = DiscUtilityBase.lookForServer(guild, engine);
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return true;
        }

        for (String vcI : server.getAutoChannels()) {
            if (vcI.equals(channelJoined.getId())) {
                VoiceChannel vc = channelJoined;
                AutoChannel ac = new AutoChannel().createAutoChan(vc, guild, entity);
                activeAutoChannels.add(ac);
                return true;
            }
        }

        for (String vcI : server.getGamingChannels()) {
            if (vcI.equals(channelJoined.getId())) {
                VoiceChannel vc = channelJoined;
                AutoChannel ac = new AutoChannel().createGamingChan(vc, guild, entity);
                activeAutoChannels.add(ac);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        DiscApplicationServer server = DiscUtilityBase.lookForServer(event.getGuild(), engine);
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vc : server.getAutoChannels()) {
            if (vc.equals(event.getChannel().getId())) {
                server.removeAutoChannel(vc);
                server.removeGamingChannel(vc);
            }
        }

        Iterator<AutoChannel> iterator = activeAutoChannels.iterator();
        while (iterator.hasNext()) {
            AutoChannel ac = iterator.next();
            VoiceChannel vc = ac.getVc();
            if (vc.getId().equals(event.getChannel().getId())) {
                iterator.remove();
                vc.delete().queue();
            }
        }
    }

    @Override
    public void onUserActivityStart(@NotNull UserActivityStartEvent event) {
        AutoChannel ac = getAutoChan(event.getMember());
        if (ac != null) {
            if (ac.getType() == AutoChannel.AutoChannelType.Gaming)
                if (ac.getCreatedBy().getId().equals(event.getUser().getId())) {
                    if (!ac.isAskedForChange()) {
                        String game = DiscUtilityBase.getGame(event.getMember());
                        if (game == null)
                            return;

                        MessageEmbed m = new EmbedBuilder().setColor(Color.MAGENTA).setTitle("Gaming Channel").setDescription("Hey " + event.getMember().getNickname() + " it seems like you are playing in a channel.\n\nWould you like to rename the current auto channel to `" + game + "`?").build();
                        PrivateChannel pc = event.getMember().getUser().openPrivateChannel().complete();
                        Message msg = pc.sendMessage(m).complete();
                        msg.addReaction("✅").queue();
                        msg.addReaction("❌").queue();
                        ac.setAskedForChange(true);

                        Response response = new Response(Response.ResponseTyp.Discord) {
                            @Override
                            public void onPrivateEmote(PrivateMessageReactionAddEvent respondingEvent) {
                                switch (respondingEvent.getReactionEmote().getName()) {
                                    //haken-Emoji
                                    case "✅":
                                        try {
                                            ac.rename(game);
                                        } catch (Exception e) {
                                            msg.delete().queue();
                                            break;
                                        }
                                        engine.getDiscEngine().getTextUtils().sendSucces("Successfully changed channel name!", respondingEvent.getChannel(), 10 * 10 * 10 * 10);
                                        msg.delete().queue();
                                        break;

                                    //X-Emoji
                                    case "❌":
                                        msg.delete().queue();
                                        break;
                                }
                            }
                        };
                        response.discUserId = event.getUser().getId();
                        response.discChannelId = pc.getId();
                        engine.getResponseHandler().makeResponse(response);
                    }
                } else {
                    if (ac.getVc().getMembers().size() >= 2)
                        if (!ac.isWasRenamedByMember()) {
                            ArrayList<String> games = new ArrayList<>();
                            for (Member m : ac.getVc().getMembers()) {
                                String g = DiscUtilityBase.getGame(m);
                                if (g.length() > 1)
                                    games.add(g);
                            }
                            String most = "";
                            int mostI = 0;
                            for (String s : games) {
                                int i = games.indexOf(s);
                                if (i > mostI) {
                                    most = s;
                                    mostI = i;
                                    games.removeIf(e -> e.equals(s));
                                }
                            }

                            if (games.size() / 2 > mostI)
                                ac.rename(most);
                        }
                }
        }
    }

    private AutoChannel getAutoChan(Member m) {
        Iterator<AutoChannel> iterator = activeAutoChannels.iterator();
        while (iterator.hasNext()) {
            AutoChannel ac = iterator.next();
            VoiceChannel vc = ac.getVc();
            VoiceChannel svc = m.getVoiceState().getChannel();
            if (svc == null)
                return null;
            if (svc.getId().equals(vc.getId())) {
                return ac;
            }
        }
        return null;
    }

    /* Feature deleted by member request
    @Override
    public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {
        if(scheudlers.containsKey(event.getMember().getId())){
            scheudlers.get(event.getMember().getId()).shutdown();
            scheudlers.remove(event.getMember().getId());
        }
        
        DiscApplicationServer server = DiscUtilityBase.lookForServer(event.getGuild(), engine);
        if(event.isSelfDeafened()){
            Member m = event.getMember();
            Runnable task = () -> {
                if(!m.getVoiceState().isDeafened())
                    return;
                deafens.put(event.getMember().getId(), event.getVoiceState().getChannel());
                if(server.isMoveMemberOnSDeafen()){
                    VoiceChannel afk = event.getGuild().getAfkChannel();
                    if(afk != null){
                        event.getGuild().moveVoiceMember(event.getMember(), afk).queue();
                    }
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(task, 10, TimeUnit.MINUTES);
            scheudlers.put(event.getMember().getId(), executor);
        } else {
            VoiceChannel v = deafens.get(event.getMember().getId());
            if(v != null){
                if(event.getMember().getVoiceState().getChannel().getId().equals(event.getGuild().getAfkChannel().getId())){
                    event.getGuild().moveVoiceMember(event.getMember(), v).queue();
                }
            }
            deafens.remove(event.getMember().getId());
        }
    }
     */


    public boolean renameAutoChannelByUser(User user, String newName) {
        for (Guild g : engine.getDiscEngine().getBotJDA().getGuilds()) {
            Member m = g.getMember(user);
            if (m == null)
                continue;
            AutoChannel ac = getAutoChan(m);
            if (ac == null)
                continue;
            ac.rename(newName);
            ac.setWasRenamedByMember(true);
            return true;
        }
        return false;
    }


    private void testActiveVC(VoiceChannel vc) {
        Iterator<AutoChannel> iterator = activeAutoChannels.iterator();
        while (iterator.hasNext()) {
            AutoChannel ac = iterator.next();
            VoiceChannel vcc = ac.getVc();
            if (vc.getId().equals(vcc.getId()) && vc.getMembers().size() == 0) {
                iterator.remove();
                vc.delete().queue();
            }
        }
    }

    public ArrayList<String> getAutoChanList() {
        ArrayList<String> ids = new ArrayList<>();
        for (AutoChannel ac : activeAutoChannels) {
            VoiceChannel vc = ac.getVc();
            ids.add(vc.getId());
        }
        return ids;
    }

    public void loadAutoChans(ArrayList<String> ids, DiscApplicationServer server) {
        for (String s : ids) {
            VoiceChannel vc = null;
            try {
                vc = engine.getDiscEngine().getBotJDA().getVoiceChannelById(s);
            } catch (Exception e) {
            }
            if (vc != null) {
                if (vc.getMembers().size() == 0) {
                    vc.delete().queue();
                } else {
                    AutoChannel ac = new AutoChannel().recreate(vc, AutoChannel.AutoChannelType.Basic);
                    activeAutoChannels.add(ac);
                }
            }
        }

        //Is in (voice create channel)
        if (server != null) {
            for (String id : server.getAutoChannels()) {
                VoiceChannel auto = engine.getDiscEngine().getBotJDA().getVoiceChannelById(id);
                if (auto != null) {
                    if (auto.getMembers().size() > 0) {
                        Member m0 = null;
                        m0 = auto.getMembers().get(0);
                        auto.getGuild().moveVoiceMember(m0, auto.getGuild().getAfkChannel()).complete();
                        auto.getGuild().moveVoiceMember(m0, auto).complete();
                        Timer t = new Timer();
                        String member0Id = m0.getId();
                        TimerTask tt = new TimerTask() {
                            @Override
                            public void run() {
                                Member m = auto.getGuild().getMemberById(member0Id);
                                if (auto.getMembers().size() > 0)
                                    for (int i = 0; i < auto.getMembers().size(); i++) {
                                        try {
                                            auto.getGuild().moveVoiceMember(auto.getMembers().get(i), m.getVoiceState().getChannel()).queue();
                                        } catch (Exception ignored) {
                                        }
                                    }
                            }
                        };
                        t.schedule(tt, 10 * 10 * 10 * 2);
                    }
                }
            }

        }
    }

    private void playingGame(VoiceChannel vc) {
        for (Member m : vc.getMembers()) {
            for (Activity ac : m.getActivities()) {
                //TODO: is this right?
                ac.asRichPresence().getName();
            }
        }
    }
}
