package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public class DiscCmdAutoChannel implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return DiscUtilityBase.userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel(), engine);
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "add":
                    if (args.length >= 2) {
                        VoiceChannel newAutoChannel = event.getGuild().getVoiceChannelById(args[1]);
                        if (newAutoChannel != null) {
                            if (server.getAutoChannels().contains(newAutoChannel.getId())) {
                                engine.getDiscEngine().getTextUtils().sendError("This channel is already registerd!", event.getChannel(), false);
                                return;
                            }
                            server.addAutoChannel(newAutoChannel.getId());
                            engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.autochan.success.created", user.getLang(), null), event.getChannel());
                        } else {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "gaming": {
                    if (args.length >= 2) {
                        VoiceChannel newAutoChannel = event.getGuild().getVoiceChannelById(args[1]);
                        if (newAutoChannel == null) {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
                            return;
                        }
                        for (String vc : server.getAutoChannels()) {
                            if (vc.equals(newAutoChannel.getId())) {
                                server.removeAutoChannel(vc);
                                server.addGamingChannel(vc);
                                break;
                            }
                        }
                        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.autochan.success.created", user.getLang(), null), event.getChannel());
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
                    }
                }
                break;

                case "remove":
                    if (args.length >= 2) {
                        VoiceChannel newAutoChannel = event.getGuild().getVoiceChannelById(args[1]);
                        if (newAutoChannel != null) {
                            server.removeAutoChannel(newAutoChannel.getId());
                            server.removeGamingChannel(newAutoChannel.getId());
                            engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.autochan.success.removed", user.getLang(), null), event.getChannel());
                        } else {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "list":
                    String channels = "**Auto Channels**\n\n";
                    int c = 1;
                    for (String vc : server.getAutoChannels()) {
                        channels = channels + c + ": `[" + event.getGuild().getVoiceChannelById(vc).getName() + "]`\n";
                        c++;
                    }
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(channels, event.getChannel(), "Auto channels", Color.blue);
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
            }
        } else {
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
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
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.autochan.help", user.getLang(), null);
    }

    @Override
    public CommandData getCommand() {
        return null;
    }

    @Override
    public String getInvoke() {
        return "autochan";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
