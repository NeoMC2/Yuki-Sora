package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.collectables.metal.Metal;
import botApplication.discApplication.librarys.item.crafting.Smelter;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.HashMap;

public class DiscCmdSmelter implements DiscCommand {

    HashMap<String, Smelter> smelter = new HashMap<>();

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if(args.length>0)
            switch (args[0]){
                case "smelt":
                    Item item;
                    try {
                        engine.getDiscEngine().getTextUtils().sendError("This Item is invalid!", event.getChannel(), false);
                        item = user.getItems().get(Integer.parseInt(args[1]));
                    } catch (Exception e){
                        return;
                    }

                    Metal metal;

                    try {
                        metal = (Metal) item;
                    } catch (Exception e){
                        engine.getDiscEngine().getTextUtils().sendError("This Item is no Metal!", event.getChannel(), false);
                        return;
                    }

                    if(smelter.containsKey(event.getMember().getId())){
                        engine.getDiscEngine().getTextUtils().sendError("You are currently smelting another Item!", event.getChannel(), false);
                        return;
                    }

                    user.getItems().remove(item);
                    Smelter sm = new Smelter(metal, event.getMember());
                    sm.startSmelting();
                    smelter.put(event.getMember().getId(), sm);
                    break;

                case "open":
                    Smelter smelt = smelter.get(event.getMember().getId());
                    Metal readyMetal;
                    try {
                        readyMetal = smelt.open();
                    } catch (Exception e){
                        engine.getDiscEngine().getTextUtils().sendError("Your Metal is not ready yet!", event.getChannel(), false);
                        return;
                    }
                    try {
                        user.addItem(readyMetal);
                    } catch (Exception e) {
                        engine.getDiscEngine().getTextUtils().sendWarining("Your inventory is full! ", event.getChannel());
                    }
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
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
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
