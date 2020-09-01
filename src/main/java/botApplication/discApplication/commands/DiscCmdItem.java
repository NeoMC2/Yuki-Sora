package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.collectables.gems.Diamond;
import botApplication.discApplication.librarys.item.collectables.metal.Copper;
import botApplication.discApplication.librarys.item.collectables.metal.Iron;
import botApplication.discApplication.librarys.item.collectables.metal.Metal;
import botApplication.discApplication.librarys.item.collectables.stuff.Cable;
import botApplication.discApplication.librarys.item.collectables.stuff.Tape;
import botApplication.discApplication.librarys.item.crafting.CraftItem;
import botApplication.discApplication.librarys.item.crafting.CraftingRecipe;
import botApplication.discApplication.librarys.item.crafting.Smelter;
import com.iwebpp.crypto.TweetNaclFast;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DiscCmdItem implements DiscCommand {

    HashMap<String, CraftingRecipe> craftingRecipes = new HashMap<>();

    public DiscCmdItem() {
        CraftingRecipe em = new CraftingRecipe();

        em.ingredients.add(new CraftItem(new Cable()));
        em.ingredients.add(new CraftItem(new Tape()));
        em.ingredients.add(new CraftItem(new Diamond()));
        em.ingredients.add(new CraftItem(new Iron()));
    }

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

                case "craft":
                    CraftingRecipe c = craftingRecipes.get(args[1]);
                    if(c==null){
                        engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", event.getChannel(), false);
                        return;
                    }
                    Item cr = null;
                    try {
                        cr = c.craft(user.getItems());
                    } catch (Exception e) {
                        engine.getDiscEngine().getTextUtils().sendError("You don't have enough Items to craft the new Item!", event.getChannel(), false);
                        return;
                    }
                    user.getItems().add(cr);
                    engine.getDiscEngine().getTextUtils().sendSucces("You've got " + cr.getItemName() + " (" + cr.getItemRarity().name() + ")!", event.getChannel());
                    break;

                case "craftinfo":
                    CraftingRecipe craftRec = craftingRecipes.get(args[1]);
                    if(craftRec==null){
                        engine.getDiscEngine().getTextUtils().sendError("Crafting recipe doesn't exist!", event.getChannel(), false);
                        return;
                    }
                    String msgI = "For:\n" + craftRec.result.getItemName() + " (" + craftRec.resAmount + ")\n\nYou will need:\n";
                    for(CraftItem cItem: craftRec.ingredients){
                        msgI += cItem.item.getItemName() + " (" + cItem.amount + ")\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendChannelConsolMessage(msgI, event.getChannel());
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
