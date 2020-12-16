package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONObject;

public class DiscCmdMonster implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {

            switch (args[0].toLowerCase()) {

                case "buy":
                    user.substractCoins(20);
                    JSONObject res = engine.getDiscEngine().getApiManager().userRandomMonster(user.getUserId(), "normal");
                    JSONObject mnster = (JSONObject) res.get("data");
                    String mnsterName = (String) mnster.get("name");
                    String rar = (String) mnster.get("rarity");

                    engine.getDiscEngine().getTextUtils().sendCustomMessage("You've got " + mnsterName + " " + rar, event.getChannel(), "New Monster", DiscCmdItem.rarityToColor(rar));
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
        return engine.lang("cmd.pokemon.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
