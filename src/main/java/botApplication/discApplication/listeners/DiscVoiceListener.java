package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.ArrayList;

public class DiscVoiceListener extends ListenerAdapter {

    private Engine engine;

    public static ArrayList<VoiceChannel> active = new ArrayList<>();

    public DiscVoiceListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if(server == null){
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vcI:server.getAutoChannels()) {
            if(vcI.equals(event.getChannelJoined().getId())){
                VoiceChannel vc = event.getGuild().getVoiceChannelById(vcI);
                setupVc(vc, event.getGuild().getController(), event.getMember());
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
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if(server == null){
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vcI: server.getAutoChannels()) {
            if(vcI.equals(event.getChannelJoined().getId())){
                VoiceChannel vc = event.getGuild().getVoiceChannelById(vcI);
                setupVc(vc, event.getGuild().getController(), event.getMember());
            }
        }

        VoiceChannel vc = event.getChannelLeft();
        testActiveVC(vc);

    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if(server == null){
            engine.getUtilityBase().printOutput("[Guld Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vc: server.getAutoChannels()) {
            if(vc.equals(event.getChannel().getId())){
                server.getAutoChannels().remove(vc);
            }
        }

        for (VoiceChannel vc: active) {
            if(vc.getId().equals(event.getChannel().getId())){
                server.getAutoChannels().remove(vc);
            }
        }
    }

    private void setupVc(VoiceChannel vc, GuildController gc, Member m){
        VoiceChannel nvc = (VoiceChannel)gc.createVoiceChannel(vc.getName() + " [AC]")
                .setBitrate(vc.getBitrate())
                .setUserlimit(vc.getUserLimit())
                .complete();

        if (vc.getParent() != null)
            nvc.getManager().setParent(vc.getParent()).queue();

        gc.modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
        gc.moveVoiceMember(m, nvc).queue();
        for (PermissionOverride or:vc.getPermissionOverrides()) {
            nvc.createPermissionOverride(or.getRole()).setAllow(or.getAllowed()).setDeny(or.getDenied()).queue();
        }

        active.add(nvc);
    }

    private void testActiveVC(VoiceChannel vc){
        if (active.contains(vc) && vc.getMembers().size() == 0) {
            active.remove(vc);
            vc.delete().queue();
        }
    }
}
