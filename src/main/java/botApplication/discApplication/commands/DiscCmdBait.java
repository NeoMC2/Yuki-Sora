package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;

public class DiscCmdBait implements DiscCommand {

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return engine.getDiscEngine().getUtilityBase().userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel());
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {

        if (args[0].toLowerCase().equals("remove")) {
            ArrayList<Bait> baits = engine.getDiscEngine().getFilesHandler().getBaits();
            for (Bait bait : baits) {
                if (bait.baider.getId().equals(event.getMember().getId())) {
                    Member baittt = null;
                    try {
                        baittt = event.getMessage().getMentionedMembers().get(0);
                    } catch (Exception e) {
                    }

                    if (baittt == null)
                        try {
                            baittt = event.getGuild().getMemberById(args[1]);
                        } catch (Exception e) {
                        }

                    if (baittt.getId().equals(bait.bait.getId())) {
                        engine.getDiscEngine().getFilesHandler().getBaits().remove(bait);
                        engine.getDiscEngine().getTextUtils().sendSucces("Removed" + bait.bait.getNickname() + " was removed!", event.getChannel());
                        break;
                    }
                }
            }
            return;
        }

        if (server.getBaitChannel() == null) {
            engine.getDiscEngine().getTextUtils().sendError("This server has no bait channel!", event.getChannel(), false);
            return;
        }

        Member bait = null;
        try {
            bait = event.getMessage().getMentionedMembers().get(0);
        } catch (Exception e) {
        }

        if (bait == null)
            try {
                bait = event.getGuild().getMemberById(args[0]);
            } catch (Exception e) {
            }

        if(bait == null){
            engine.getDiscEngine().getTextUtils().sendError("Member not found!", event.getChannel(), false);
            return;
        }

        for (Bait b : engine.getDiscEngine().getFilesHandler().getBaits()) {
            if (b.bait.getId().equals(bait))
                if (b.baider.getId().equals(event.getMember().getId())) {
                    engine.getDiscEngine().getTextUtils().sendError("You've already baided that member!", event.getChannel(), false);
                    return;
                }
        }

        Bait b = new Bait();
        b.baider = event.getMember();
        b.bait = bait;

        engine.getDiscEngine().getFilesHandler().getBaits().add(b);

        for (Member m : bait.getVoiceState().getChannel().getMembers()) {
            if (m.getId().equals(event.getMember().getId())) {
                VoiceChannel vc = event.getGuild().getVoiceChannelById(server.getBaitChannel());
                event.getGuild().moveVoiceMember(bait, vc).queue();
                break;
            }
        }

        engine.getDiscEngine().getTextUtils().sendSucces("Baided " + b.bait.getNickname() + "!", event.getChannel());
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

    public class Bait {
        public Member bait;
        public Member baider;
    }
}
