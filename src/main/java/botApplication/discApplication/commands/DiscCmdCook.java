package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;
import botApplication.discApplication.librarys.item.crafting.Cooking;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class DiscCmdCook implements DiscCommand {

    HashMap<String, Cooking> smelter = new HashMap<>();

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        perform(args, event.getAuthor(), user, engine, null, event.getChannel());
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        perform(args, event.getAuthor(), user, engine, event.getChannel(), null);
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void perform(String[] args, User u, DiscApplicationUser user, Engine engine, PrivateChannel pc, TextChannel tc) {
        if (args.length > 0)
            switch (args[0]) {
                case "smelt":
                    Item item;
                    try {
                        item = user.getItems().get(Integer.parseInt(args[1]) - 1);
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.invalidItem", user.getLang(), null), pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.invalidItem", user.getLang(), null), tc, false);
                        return;
                    }

                    Cookable metal;

                    try {
                        metal = (Cookable) item;
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.cantCook", user.getLang(), null), pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.cantCook", user.getLang(), null), tc, false);
                        return;
                    }

                    if (smelter.containsKey(u.getId())) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.cookingAlready", user.getLang(), null), pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.cookingAlready", user.getLang(), null), tc, false);
                        return;
                    }

                    user.getItems().remove(item);
                    Cooking sm = new Cooking(metal, u);
                    sm.startCooking();
                    smelter.put(u.getId(), sm);
                    EmbedBuilder b = new EmbedBuilder().setImage(engine.lang("cmd.cook.info.nowCooking", user.getLang(), new String[]{item.getItemName(), String.valueOf(metal.cookTime())})).setTitle("Smelting");
                    if (pc != null)
                        pc.sendMessage(b.build()).queue();
                    else
                        tc.sendMessage(b.build()).queue();
                    break;

                case "open":
                    Cooking smelt = smelter.get(u.getId());
                    Item readyItem;

                    if (smelt == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.empty", user.getLang(), null), pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.empty", user.getLang(), null), tc, false);
                        return;
                    }
                    try {
                        readyItem = smelt.open();
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.notReady", user.getLang(), null), pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.cook.error.notReady", user.getLang(), null), tc, false);
                        return;
                    }
                    try {
                        user.addItem(readyItem);
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendWarining("Your inventory is full! ", pc);
                        else
                            engine.getDiscEngine().getTextUtils().sendWarining("Your inventory is full! ", tc);
                    }
                    EmbedBuilder bb = new EmbedBuilder().setImage(readyItem.getImgUrl()).setColor(Color.GREEN).setFooter(readyItem.getItemName()).setTitle("Finish");
                    if (pc != null)
                        pc.sendMessage(bb.build()).queue();
                    else
                        tc.sendMessage(bb.build()).queue();
                    break;

                default:
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), pc, false);
                    else
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), tc, false);
                    break;
            }
    }

}
