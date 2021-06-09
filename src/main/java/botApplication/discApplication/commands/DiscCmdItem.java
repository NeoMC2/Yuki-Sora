package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class DiscCmdItem implements DiscCommand {

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        ArrayList<User> users = new ArrayList<>();
        users.addAll(event.getMessage().getMentionedUsers());
        event.getChannel().sendMessage(perform(args, engine, user, users, event.getGuild())).queue();
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        ArrayList<User> users = new ArrayList<>();
        users.addAll(event.getMessage().getMentionedUsers());
        event.getChannel().sendMessage(perform(args, engine, user, users, null)).queue();
    }

    @Override
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        ArrayList<User> users = new ArrayList<>();
        for (OptionMapping mapping:event.getOptionsByType(OptionType.USER)) {
            users.add(mapping.getAsUser());
        }
        event.getHook().sendMessageEmbeds(perform(args,engine,user, users, event.getGuild())).queue();
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.item.help", user.getLang(), null);
    }

    @NotNull
    @Override
    public CommandData getCommand() {
        return new CommandData(getInvoke(), "item").addSubcommands(
                new SubcommandData("list", "Shows the list of your items"),
                new SubcommandData("give", "Gives an item to another user").addOption(OptionType.INTEGER, "item", "The ID in your storage", true).addOption(OptionType.USER, "user", "The user you want to give the item", true),
                new SubcommandData("trash", "Destroy an item of yours").addOption(OptionType.INTEGER, "item", "The ID in your storage", true),
                new SubcommandData("info", "Shows information about a specific item").addOption(OptionType.INTEGER, "item", "The ID in your storage", true)
        );
    }

    @Override
    public String getInvoke() {
        return "item";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private MessageEmbed perform(String[] args, Engine engine, DiscApplicationUser user, ArrayList<User> users, Guild g) {
        if (args.length > 0)
            switch (args[0].toLowerCase()) {
                case "list":
                    JSONObject res = engine.getDiscEngine().getApiManager().getUserInventoryById(user.getUserId());
                    if ((Long) res.get("status") != 200) {
                        return new EmbedBuilder().setColor(Color.RED).setDescription("No inventory found").build();
                    }

                    String itemList = "";
                    JSONArray dat = (JSONArray) res.get("data");
                    for (Object o : dat) {
                        JSONObject sto = (JSONObject) o;
                        itemList += sto.get("itemName") + " Amount: " + sto.get("amount") + "\n";
                    }
                    return new EmbedBuilder().setColor(Color.BLUE).setDescription(itemList).setAuthor("List").build();

                case "give":
                    if(g == null){
                        return new EmbedBuilder().setColor(Color.RED).setDescription("This command can only be called on guilds!").build();
                    }
                    String item = args[1];

                    Member memberUsr;
                    memberUsr = g.getMember(users.get(0));
                    DiscApplicationUser giveUsr = null;
                    if (memberUsr == null)
                        try {
                            engine.getDiscEngine().getBotJDA().getUserById(args[2]);
                            giveUsr = engine.getDiscEngine().getFilesHandler().getUserById(memberUsr.getId());
                        } catch (Exception e) {
                            return new EmbedBuilder().setColor(Color.RED).setDescription("Member not found!").build();
                        }
                    if (giveUsr == null) {
                        return new EmbedBuilder().setColor(Color.RED).setDescription("Member not found!").build();
                    }

                    JSONObject it = getItemByName(item, engine, user);

                    if (it == null) {
                        return new EmbedBuilder().setColor(Color.RED).setDescription("Item not found!").build();
                    }

                    JSONObject resIt = engine.getDiscEngine().getApiManager().removeItemFromUser((String) it.get("item"), user.getUserId(), 1);
                    if ((Long) resIt.get("status") == 200) {
                        JSONObject resItGi = engine.getDiscEngine().getApiManager().addItemToUser((String) it.get("item"), memberUsr.getId(), 1);
                        if(((Long) resItGi.get("status")) != 200){
                            engine.getDiscEngine().getApiManager().addItemToUser((String) it.get("item"),user.getUserId(), 1);
                            return new EmbedBuilder().setColor(Color.RED).setDescription("The Item can't be given to that user!").build();
                        }
                    } else {
                        return new EmbedBuilder().setColor(Color.RED).setDescription("Can't give that item!").build();
                    }

                    return new EmbedBuilder().setAuthor(user.getUserName() + " gave " + it.get("itemName") + " to " + memberUsr.getNickname(), null, memberUsr.getUser().getAvatarUrl()).setColor(Color.GREEN).setImage((String) it.get("itemImageURL")).build();

                case "trash":
                case "remove":
                    String removingItem = args[1];
                    JSONObject itt = getItemByName(removingItem, engine, user);
                    JSONObject r = engine.getDiscEngine().getApiManager().removeItemFromUser((String) itt.get("_id"), user.getUserId(), 1);

                    if ((Long) r.get("status") != 200) {
                        return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.invalidItem", user.getLang(), null)).build();
                    }

                    return new EmbedBuilder().setColor(Color.GREEN).setDescription("Removed!").build();

                case "info":
                    JSONObject infoItem = getItemByName(args[1], engine, user);
                    if (infoItem == null) {
                        return new EmbedBuilder().setColor(Color.RED).setDescription("Item not found!").build();
                    }

                    String des = (String) infoItem.get("itemDescription");
                    if (des == null)
                        des = "no description!";

                    return new EmbedBuilder().setTitle((String) infoItem.get("itemName")).setColor(rarityToColor((String) infoItem.get("itemRarity"))).setDescription("Description:\n`" + des + "`").setImage((String) infoItem.get("itemImageURL")).build();

                default:
                    return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
            }
        return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
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
        switch (rarity.toLowerCase()){
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
