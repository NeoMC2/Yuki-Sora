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

import java.awt.*;
import java.util.ArrayList;

public class DiscCmdWallet implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        ArrayList<User> users = new ArrayList<>(event.getMessage().getMentionedUsers());
        event.getChannel().sendMessage(perform(users, event.getGuild(), engine, args, user)).queue();
    }

    private MessageEmbed perform(ArrayList<User> users, Guild g, Engine engine, String[] args, DiscApplicationUser user){
        if (args.length >= 1) {
            switch (args[0]) {
                case "money":
                case "coins":
                case "amount":
                case "info":
                    return new EmbedBuilder().setColor(Color.YELLOW).setDescription(engine.lang("cmd.wallet.info.coins", user.getLang(), new String[]{String.valueOf(user.getCoins())})).build();

                case "give":
                    if(users == null || g == null){
                        return new EmbedBuilder().setColor(Color.RED).setDescription("This command can only be performed on guilds!").build();
                    }
                    if (args.length >= 3) {
                        Member m;
                        if (users.size() != 0) {
                            m = g.getMember(users.get(0));
                        } else {
                            m = g.getMemberById(args[1]);
                        }

                        DiscApplicationUser usr;
                        try {
                            usr = engine.getDiscEngine().getFilesHandler().getUserById(m.getUser().getId());
                        } catch (Exception e) {
                            return new EmbedBuilder().setColor(Color.RED).setDescription("Member not found!").build();
                        }
                        int c;
                        try {
                            c = Integer.parseInt(args[2]);
                        } catch (Exception e) {
                            return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.invalidArgument", user.getLang(), null)).build();
                        }
                        if (c > user.getCoins()) {
                            return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("cmd.wallet.error.notEnoughMoney", user.getLang(), null)).build();
                        }
                        try {
                            user.substractCoins(c, engine);
                        } catch (Exception e) {
                            return new EmbedBuilder().setColor(Color.RED).setDescription("You don't have enough coins!").build();
                        }
                        usr.addCoins(c, engine);
                        return new EmbedBuilder().setColor(Color.GREEN).setDescription(engine.lang("cmd.wallet.success.gaveMoney", user.getLang(), new String[]{String.valueOf(c), m.getUser().getName()})).setAuthor("Success").build();
                    } else {
                        return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.notEnoughArgs", user.getLang(), null)).build();
                    }

                default:
                    return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
            }
        }
        return new EmbedBuilder().setColor(Color.RED).setDescription(engine.lang("general.error.404cmdArg", user.getLang(), null)).build();
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        event.getChannel().sendMessage(perform(null, null, engine, args, user)).queue();
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
        event.getHook().sendMessageEmbeds(perform(users, event.getGuild(), engine, args, user)).queue();
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.wallet.help", user.getLang(), null);
    }

    @NotNull
    @Override
    public CommandData getCommand() {
        return new CommandData(getInvoke(), "Manage your wallet").addSubcommands(
                new SubcommandData("info", "Shows how many coins you have"),
                new SubcommandData("give", "Gives money to another member").addOption(OptionType.STRING, "coins", "The amount of coins you want to give", true).addOption(OptionType.USER, "user", "The member you want to give your money", true)
        );
    }

    @Override
    public String getInvoke() {
        return "wallet";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
