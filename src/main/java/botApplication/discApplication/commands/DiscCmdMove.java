package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class DiscCmdMove implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            return true;
        } else {
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.noadmin", user.getLang(), null), event.getChannel(), engine.getProperties().middleTime, false);
            return false;
        }
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 2) {
            if (args[0].equals("us")) {
                VoiceChannel vc = event.getMember().getVoiceState().getChannel();
                VoiceChannel to = event.getGuild().getVoiceChannelById(args[1]);
                if (vc == null) {
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notInValidChannel", user.getLang(), null), event.getChannel(), false);
                } else {
                    if (to != null) {
                        for (Member m : vc.getMembers()) {
                            event.getGuild().getController().moveVoiceMember(m, to).queue();
                        }
                        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.move.succes.moved", user.getLang(), null), event.getChannel());
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
                    }
                }
            }
        } else {
            VoiceChannel vc = event.getGuild().getVoiceChannelById(args[0]);
            VoiceChannel to = event.getGuild().getVoiceChannelById(args[1]);
            if(vc == null) {
                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
            } else if (to == null) {
                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
            } else {
                for (Member m : vc.getMembers()) {
                    event.getGuild().getController().moveVoiceMember(m, to).queue();
                }
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
        return engine.lang("cmd.move.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void moveAllMembers(VoiceChannel from, VoiceChannel too, Guild guild) {
        for (Member m : from.getMembers()) {
            guild.getController().moveVoiceMember(m, too).queue();
        }
    }
}
