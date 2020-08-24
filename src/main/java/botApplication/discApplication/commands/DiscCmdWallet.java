package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;

public class DiscCmdWallet implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "money":
                case "coins":
                case "amount":
                case "info":
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.wallet.info.coins", user.getLang(), new String[]{String.valueOf(user.getCoins())}), event.getChannel(), "Info", Color.YELLOW);
                    break;

                case "give":
                    if (args.length >= 3) {
                        Member m;
                        if (event.getMessage().getMentionedMembers().size() != 0) {
                            m = event.getMessage().getMentionedMembers().get(0);
                        } else {
                            m = event.getGuild().getMemberById(args[1]);
                        }

                        DiscApplicationUser usr;
                        try {
                            usr = engine.getDiscEngine().getFilesHandler().getUserById(m.getUser().getId());
                        } catch (Exception e) {
                            return;
                        }
                        int c;
                        try {
                            c = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("general.error.invalidArgument", user.getLang(), null), event.getChannel(), "Error", Color.RED);
                            return;
                        }
                        if (c > user.getCoins()) {
                            engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.wallet.error.notEnoughMoney", user.getLang(), null), event.getChannel(), "Error", Color.RED);
                            return;
                        }
                        user.substractCoins(c);
                        usr.addCoins(c);
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.wallet.success.gaveMoney", user.getLang(), new String[]{String.valueOf(c), m.getUser().getName()}), event.getChannel(), "Success", Color.GREEN);
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
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
        return engine.lang("cmd.wallet.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
