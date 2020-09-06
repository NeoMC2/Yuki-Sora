package botApplication.discApplication.listeners;

import botApplication.discApplication.commands.DiscCmdBait;
import botApplication.discApplication.librarys.DiscApplicationServer;
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

public class DiscVoiceListener extends ListenerAdapter {

    public static ArrayList<VoiceChannel> active = new ArrayList<>();
    private final Engine engine;

    public DiscVoiceListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        ArrayList<DiscCmdBait.Bait> baits = engine.getDiscEngine().getFilesHandler().getBaits();
        for (Member ba:event.getChannelJoined().getMembers()) {
            for (DiscCmdBait.Bait b:baits) {
                if(ba.getId().equals(b.baider.getId())){
                    if(event.getMember().getId().equals(b.bait.getId())){
                        event.getGuild().moveVoiceMember(b.bait, event.getGuild().getVoiceChannelById(server.getBaitChannel())).queue();
                    }
                }
            }
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
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guild Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        ArrayList<DiscCmdBait.Bait> baits = engine.getDiscEngine().getFilesHandler().getBaits();
        for (Member ba:event.getChannelJoined().getMembers()) {
            for (DiscCmdBait.Bait b:baits) {
                if(ba.getId().equals(b.baider.getId())){
                    if(event.getMember().getId().equals(b.bait.getId())){
                        event.getGuild().moveVoiceMember(b.bait, event.getGuild().getVoiceChannelById(server.getBaitChannel())).queue();
                    }
                }
            }
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
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        if (server == null) {
            engine.getUtilityBase().printOutput("[Guld Voice Join] !!!Fatal Server error!!!", true);
            return;
        }

        for (String vc : server.getAutoChannels()) {
            if (vc.equals(event.getChannel().getId())) {
                server.getAutoChannels().remove(vc);
            }
        }

        for (VoiceChannel vc : active) {
            if (vc.getId().equals(event.getChannel().getId())) {
                server.getAutoChannels().remove(vc);
            }
        }
    }

    private void setupVc(VoiceChannel vc, Guild gc, Member m) {
        VoiceChannel nvc = gc.createVoiceChannel(vc.getName() + " [AC]")
                .setBitrate(vc.getBitrate())
                .setUserlimit(vc.getUserLimit())
                .complete();

        if (vc.getParent() != null)
            nvc.getManager().setParent(vc.getParent()).queue();

        gc.modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
        gc.moveVoiceMember(m, nvc).queue();
        for (PermissionOverride or : vc.getPermissionOverrides()) {
            nvc.createPermissionOverride(or.getRole()).setAllow(or.getAllowed()).setDeny(or.getDenied()).queue();
        }

        active.add(nvc);
    }

    private void testActiveVC(VoiceChannel vc) {
        if (active.contains(vc) && vc.getMembers().size() == 0) {
            active.remove(vc);
            vc.delete().queue();
        }
    }
}
