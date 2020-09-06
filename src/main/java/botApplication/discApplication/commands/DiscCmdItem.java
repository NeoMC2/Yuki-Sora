package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.collectables.gems.Diamond;
import botApplication.discApplication.librarys.item.collectables.metal.Iron;
import botApplication.discApplication.librarys.item.collectables.metal.Platinum;
import botApplication.discApplication.librarys.item.collectables.stuff.Cable;
import botApplication.discApplication.librarys.item.collectables.stuff.Stick;
import botApplication.discApplication.librarys.item.collectables.stuff.Tape;
import botApplication.discApplication.librarys.item.collectables.trophy.CraftEmblem;
import botApplication.discApplication.librarys.item.crafting.CraftItem;
import botApplication.discApplication.librarys.item.crafting.CraftingRecipe;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class DiscCmdItem implements DiscCommand {

    HashMap<String, CraftingRecipe> craftingRecipes = new HashMap<>();

    public DiscCmdItem() {
        CraftingRecipe em = makeRecipe(new Cable(), new Tape(), new Diamond(), new Iron(), new Platinum());
        em.result = new CraftEmblem();

        CraftItem iron = new CraftItem(new Iron());
        iron.amount = 3;
        CraftingRecipe hammer = makeRecipe(new Stick());
        hammer.ingredients.add(iron);

        craftingRecipes.put("cemblm", em);

        craftingRecipes.put("hammer", hammer);
    }

    private CraftingRecipe makeRecipe(Item... i) {
        CraftingRecipe r = new CraftingRecipe();
        for (Item it : i) {
            r.ingredients.add(new CraftItem(it));
        }
        return r;
    }

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        perform(args, event.getChannel(), null, engine, user);
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        perform(args, null, event.getChannel(), engine, user);
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void perform(String[] args, TextChannel tc, PrivateChannel pc, Engine engine, DiscApplicationUser user) {
        if (args.length > 0)
            switch (args[0].toLowerCase()) {
                case "list":
                    String itemList = "";
                    for (int j = 0; j < user.getItems().size(); j++) {
                        Item i = user.getItems().get(j);
                        itemList += "(" + (j + 1) + ") " + i.getItemName() + " (" + i.getItemRarity().name() + ")\n";
                    }
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(itemList, pc, "list", Color.BLUE);
                    else
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(itemList, tc, "list", Color.BLUE);
                    break;

                case "trash":
                case "remove":
                    Item i;
                    try {
                        i = user.getItems().get(Integer.parseInt(args[1]) - 1);
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), pc);
                        else
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), tc);
                        return;
                    }
                    user.getItems().remove(i);
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendSucces("Removed!", pc);
                    else
                        engine.getDiscEngine().getTextUtils().sendSucces("Removed!", tc);
                    break;

                case "craft":
                    CraftingRecipe c = craftingRecipes.get(args[1].toLowerCase());
                    if (c == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", tc, false);
                        return;
                    }
                    Item cr = null;
                    try {
                        cr = c.craft(user.getItems());
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("You don't have enough Items to craft the new Item!", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("You don't have enough Items to craft the new Item!", tc, false);
                        return;
                    }
                    user.getItems().add(cr);
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendSucces("You've got " + cr.getItemName() + " (" + cr.getItemRarity().name() + ")!", pc);
                    else
                        engine.getDiscEngine().getTextUtils().sendSucces("You've got " + cr.getItemName() + " (" + cr.getItemRarity().name() + ")!", tc);
                    break;

                case "craftinfo":
                    CraftingRecipe craftRec = craftingRecipes.get(args[1]);
                    if (craftRec == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", tc, false);
                        return;
                    }
                    String msgI = "For:\n" + craftRec.result.getItemName() + " (" + craftRec.resAmount + ")\n\nYou will need:\n";
                    for (CraftItem cItem : craftRec.ingredients) {
                        msgI += cItem.item.getItemName() + " (" + cItem.amount + ")\n";
                    }
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendChannelConsolMessage(msgI, pc);
                    else
                        engine.getDiscEngine().getTextUtils().sendChannelConsolMessage(msgI, tc);
                    break;

                case "info":
                    Item it;
                    try {
                        it = user.getItems().get(Integer.parseInt(args[1]) - 1);
                    } catch (Exception e) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), pc);
                        else
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), tc);
                        return;
                    }
                    String des = it.getDescription();
                    if(des == null)
                        des = "no description!";

                    EmbedBuilder bb = new EmbedBuilder().setTitle(it.getItemName()).setColor(Item.rarityToColor(it.getItemRarity())).setDescription("Description:\n`" + des + "`").setImage(it.getImgUrl());
                    if(pc != null)
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
        else if (pc != null)
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), pc, false);
        else
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), tc, false);
    }
}
