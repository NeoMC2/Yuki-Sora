package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.codehaus.plexus.util.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.URL;

public class DiscCmdInfo implements DiscCommand{
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        try {
            dispatchCommand(engine, event.getAuthor(), event.getChannel());
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendWarining("Something went wrong", event.getChannel());
        }
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        try {
            dispatchCommand(engine, event.getAuthor(), event.getChannel());
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendWarining("Something went wrong", event.getChannel());
        }
    }

    @Override
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public CommandData getCommand() {
        return null;
    }

    @Override
    public String getInvoke() {
        return "info";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {
    }

    private void dispatchCommand(Engine engine, User user, MessageChannel tc) throws Exception {
        JSONObject res = engine.getDiscEngine().getApiManager().getUserLevelInfo(user.getId(), user.getAvatarUrl());
        URL website = new URL((String) res.get("data"));
        File file = new File(engine.getFileUtils().home + "/temp.png");
        FileUtils.copyURLToFile(website, file);
        tc.sendFile(file, user.getName() + " info.png").queue();
        file.deleteOnExit();
    }
}
