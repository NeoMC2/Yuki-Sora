package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;

public class DiscCmdItem implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length > 0)
            switch (args[0].toLowerCase()) {
                case "list":
                    String itemList = "";
                    for (int j = 0; j < user.getItems().size(); j++) {
                        Item i = user.getItems().get(j);
                        itemList += "(" + (j+1) + ") " + i.getItemName() + " (" + i.getItemRarity().name() + ")\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(itemList, event.getChannel(), "list", Color.BLUE);
                    break;

                case "trash":
                case "remove":
                    Item i;
                    try {
                        i = user.getItems().get(Integer.parseInt(args[1]));
                    } catch (Exception e) {
                        engine.getDiscEngine().getTextUtils().sendWarining("Invalid Item", event.getChannel());
                        return;
                    }
                    user.getItems().remove(i);
                    engine.getDiscEngine().getTextUtils().sendSucces("Removed!", event.getChannel());
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
            }
        else
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
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
