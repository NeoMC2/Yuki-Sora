package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;

public class DiscCmdItem implements DiscCommand {

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        perform(args, event.getChannel(), null, engine, user, event.getMessage());
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        perform(args, null, event.getChannel(), engine, user, event.getMessage());
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.item.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void perform(String[] args, TextChannel tc, PrivateChannel pc, Engine engine, DiscApplicationUser user, Message message) {
        if (args.length > 0)
            switch (args[0].toLowerCase()) {
                case "list":
                    JSONObject res = engine.getDiscEngine().getApiManager().getUserInventoryById(user.getUserId());
                    if ((Long) res.get("status") != 200) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("No inventory found", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("No inventory found", tc, false);
                        return;
                    }

                    String itemList = "";
                    JSONArray dat = (JSONArray) res.get("data");
                    for (Object o : dat) {
                        JSONObject sto = (JSONObject) o;
                        itemList += sto.get("itemName") + " Amount: " + sto.get("amount");
                    }
                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(itemList, pc, "list", Color.BLUE);
                    else
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(itemList, tc, "list", Color.BLUE);
                    break;

                case "give":
                    String item = args[1];

                    Member memberUsr;
                    memberUsr = message.getMentionedMembers().get(0);
                    DiscApplicationUser giveUsr = null;
                    if (memberUsr == null)
                        try {
                            engine.getDiscEngine().getBotJDA().getUserById(args[2]);
                            giveUsr = engine.getDiscEngine().getFilesHandler().getUserById(memberUsr.getId());
                        } catch (Exception e) {
                            if (pc != null)
                                engine.getDiscEngine().getTextUtils().sendError("Member not found!", pc, false);
                            else
                                engine.getDiscEngine().getTextUtils().sendError("Member not found!", tc, false);
                            return;
                        }
                    if (giveUsr == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Member not found!", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Member not found!", tc, false);
                        return;
                    }

                    JSONObject it = getItemByName(item, engine, user);

                    if (it == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Item not found", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Item not found", tc, false);
                        return;
                    }

                    JSONObject resIt = engine.getDiscEngine().getApiManager().removeItemFromUser((String) it.get("item"), user.getUserId(), 1);
                    if ((Long) resIt.get("status") == 200) {
                        //TODO: what if the user you want to give the item doesnt exist?
                        engine.getDiscEngine().getApiManager().addItemToUser((String) it.get("item"), memberUsr.getId(), 1);
                    } else {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Can't give that item", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Can't give that item", tc, false);
                    }

                    EmbedBuilder builder = new EmbedBuilder().setAuthor(user.getUserName() + " gave " + it.get("itemName") + " to " + memberUsr.getNickname(), null, memberUsr.getUser().getAvatarUrl()).setColor(Color.GREEN).setImage((String) it.get("itemImageURL"));

                    if (pc != null)
                        pc.sendMessage(builder.build()).queue();
                    else
                        tc.sendMessage(builder.build()).queue();
                    break;

                case "trash":
                case "remove":
                    String removingItem = args[1];
                    JSONObject itt = getItemByName(removingItem, engine, user);
                    JSONObject r = engine.getDiscEngine().getApiManager().removeItemFromUser((String) itt.get("_id"), user.getUserId(), 1);

                    if ((Long) r.get("status") != 200) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), pc);
                        else
                            engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("general.error.invalidItem", user.getLang(), null), tc);

                        return;
                    }

                    if (pc != null)
                        engine.getDiscEngine().getTextUtils().sendSucces("Removed!", pc);
                    else
                        engine.getDiscEngine().getTextUtils().sendSucces("Removed!", tc);
                    break;

                case "info":
                    JSONObject infoItem = getItemByName(args[1], engine, user);
                    if (infoItem == null) {
                        if (pc != null)
                            engine.getDiscEngine().getTextUtils().sendError("Item not found", pc, false);
                        else
                            engine.getDiscEngine().getTextUtils().sendError("Item not found", tc, false);
                        return;
                    }

                    String des = (String) infoItem.get("itemDescription");
                    if (des == null)
                        des = "no description!";

                    EmbedBuilder bb = new EmbedBuilder().setTitle((String) infoItem.get("itemName")).setColor(rarityToColor((String) infoItem.get("itemRarity"))).setDescription("Description:\n`" + des + "`").setImage((String) infoItem.get("itemImageURL"));
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
        else if (pc != null)
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), pc, false);
        else
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), tc, false);
    }

    private JSONObject getItemByName(String name, Engine engine, DiscApplicationUser user){
        JSONObject ress = engine.getDiscEngine().getApiManager().getUserInventoryById(user.getUserId());
        if ((Long) ress.get("status") != 200) {
            return null;
        }

        JSONArray datt = (JSONArray) ress.get("data");
        JSONObject it = null;
        for (Object o : datt) {
            JSONObject sto = (JSONObject) o;
            if (((String) sto.get("itemName")).equalsIgnoreCase(name)) {
                it = sto;
                break;
            }
        }
        return it;
    }

    public static Color rarityToColor(String rarity){
        switch (rarity){
            case "normal":
                return Color.GRAY;

            case "epic":
                return Color.BLUE;

            case "legendary":
                return Color.ORANGE;

            case "mystic":
                return Color.MAGENTA;
        }
        return Color.GRAY;
    }
}
