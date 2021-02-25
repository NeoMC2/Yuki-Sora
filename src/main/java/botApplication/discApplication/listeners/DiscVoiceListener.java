package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DiscVoiceListener extends ListenerAdapter {

    public static ArrayList<VoiceChannel> active = new ArrayList<>();
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
        DiscApplicationServer server = DiscUtilityBase.lookForServer(event.getGuild(), engine);
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vcI : server.getAutoChannels()) {
            if (vcI.equals(event.getChannelJoined().getId())) {
                VoiceChannel vc = event.getGuild().getVoiceChannelById(vcI);
                setupVc(vc, event.getGuild(), event.getMember());
                return;
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel vc = event.getChannelLeft();
        testActiveVC(vc);
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        DiscApplicationServer server = DiscUtilityBase.lookForServer(event.getGuild(), engine);
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vcI : server.getAutoChannels()) {
            if (vcI.equals(event.getChannelJoined().getId())) {
                VoiceChannel vc = event.getGuild().getVoiceChannelById(vcI);
                setupVc(vc, event.getGuild(), event.getMember());
            }
        }

        VoiceChannel vc = event.getChannelLeft();
        testActiveVC(vc);

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
            }
        }

        for (VoiceChannel vc : active) {
            if (vc.getId().equals(event.getChannel().getId())) {
                server.removeAutoChannel(vc.getId());
            }
        }
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

    private void setupVc(VoiceChannel vc, Guild gc, Member m) {
        VoiceChannel nvc = gc.createVoiceChannel(vc.getName() + " [AC]")
                .setBitrate(vc.getBitrate())
                .setUserlimit(vc.getUserLimit())
                .complete();

        if (vc.getParent() != null)
            nvc.getManager().setParent(vc.getParent()).queue();

        gc.modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
        for (PermissionOverride or : vc.getPermissionOverrides()) {
            nvc.createPermissionOverride(or.getRole()).setAllow(or.getAllowed()).setDeny(or.getDenied()).complete();
        }
        gc.moveVoiceMember(m, nvc).complete();

        active.add(nvc);
    }

    private void testActiveVC(VoiceChannel vc) {
        if (active.contains(vc) && vc.getMembers().size() == 0) {
            active.remove(vc);
            vc.delete().queue();
        }
    }

    public ArrayList<String> getAutoChanList(){
        ArrayList<String> ids = new ArrayList<>();
        for (VoiceChannel vc:active) {
            ids.add(vc.getId());
        }
        return ids;
    }

    public void loadAutoChans(ArrayList<String> ids, DiscApplicationServer server){
        for (String s:ids) {
            VoiceChannel vc = null;
            try {
                vc = engine.getDiscEngine().getBotJDA().getVoiceChannelById(s);
            } catch (Exception e){
            }
            if(vc != null) {
                if(vc.getMembers().size() == 0){
                    vc.delete().queue();
                } else {
                    active.add(vc);
                }
            }
        }

        for (Guild g:engine.getDiscEngine().getBotJDA().getGuilds()) {
                if(server != null){
                    for (String id:server.getAutoChannels()) {
                        VoiceChannel auto = engine.getDiscEngine().getBotJDA().getVoiceChannelById(id);
                        if(auto != null){
                            if(auto.getMembers().size() >0){
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
                                        if(auto.getMembers().size() > 1)
                                            for (int i = 1; i < auto.getMembers().size(); i++) {
                                                try {
                                                    auto.getGuild().moveVoiceMember(auto.getMembers().get(i), m.getVoiceState().getChannel()).queue();
                                                } catch (Exception ignored){
                                                }
                                            }
                                    }};
                                t.schedule(tt, 10 * 10 * 10 * 2);
                            }
                        }
                    }
                }

        }

    }
}
