package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class DiscMessageListener extends ListenerAdapter {

    private Engine engine;

    public DiscMessageListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().length() > 2000) {
            return;
        }

        Member selfUser = event.getGuild().getMemberById(event.getGuild().getJDA().getSelfUser().getId());
        boolean commandWorked = false;

        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            if (engine.getResponseHandler().lookForResponse(event)) {
                return;
            }
            handleSecret(event);
            if (event.getMessage().getContentRaw().startsWith(".")) {
                try {
                    pictureSelect(event);
                } catch (Exception e) {
                }
                return;
            }
            if (event.getMessage().getContentRaw().startsWith(engine.getProperties().discBotApplicationPrefix)) {
                boolean hasPermission = false;
                try {
                    hasPermission = selfUser.hasPermission(Permission.ADMINISTRATOR);
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + " Bot has no permissions", true);
                }

                if (hasPermission) {
                    //command exist check
                    for (int i = 0; engine.getDiscEngine().getCommandHandler().commandIvokes.size() > i; i++) {
                        if (event.getMessage().getContentRaw().contains(engine.getDiscEngine().getCommandHandler().commandIvokes.get(i))) {
                            sendGuildCommand(event);
                            //event.getMessage().delete().queue();
                            commandWorked = true;
                            break;
                        }
                    }
                    if (!commandWorked) {
                        engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + "command " + event.getMessage().getContentRaw() + " doesnt exist!", true);
                        //engine.getDiscEngine().getTextUtils().deletUserMessage(1, event);
                    }
                } else {
                    engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + " bot has not the permission!", true);
                    engine.getDiscEngine().getTextUtils().sendError("Bot has no permission!", event.getChannel(), engine.getProperties().longTime, true);
                }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().length() > 400) {
            return;
        }
        boolean commandWorked = false;

        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            engine.getUtilityBase().printOutput(" message listener received guild message!", true);
            if (event.getMessage().getContentRaw().startsWith(engine.getProperties().discBotApplicationPrefix)) {
                //command exist check
                for (int i = 0; engine.getDiscEngine().getCommandHandler().commandIvokes.size() > i; i++) {
                    if (event.getMessage().getContentRaw().contains(engine.getDiscEngine().getCommandHandler().commandIvokes.get(i))) {
                        sendPrivateCommand(event);
                        commandWorked = true;
                        break;
                    }
                }
                if (!commandWorked) {
                    engine.getDiscEngine().getTextUtils().sendError("DicCommand " + event.getMessage().getContentRaw() + "  existiert nicht!\n\nSchreibe **" + engine.getProperties().discBotApplicationPrefix + "help** um eine auflistung der Commands zu erhalten.", event.getChannel(), engine.getProperties().middleTime, true);
                }
            }
        }
    }

    private String messageInfo(Guild guild) {
        return "[serverMessageListener -" + guild.getName() + "|" + guild.getId() + "]";
    }

    private void handleSecret(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.length() > 3) {
            String[] msgArgs = message.split(" ");

            //contains
            for (int i = 0; msgArgs.length > i; i++) {
                switch (msgArgs[i].toLowerCase()) {
                    case "anime":
                        sendNormalText(event, "Who said Anime :O");
                        return;
                    case "baka":
                        sendNormalText(event, "Baaaaaaaaaaaaaka! XD");
                        return;
                    case "jaja":
                        sendNormalText(event, "JAJA HEIßT LECK MICH AM ARSCH :D ");
                        return;
                    case "aloha":
                        sendNormalText(event, "Aloha " + event.getAuthor().getName() + " XD \nDas hat mir der Mosel beigebracht ");
                        return;
                }
            }
        }
        return;

    }

    private void sendNormalText(GuildMessageReceivedEvent event, String Message) {
        engine.getDiscEngine().getTextUtils().sendNormalTxt(Message, event.getChannel());
    }

    private void sendGuildCommand(GuildMessageReceivedEvent event) {
        engine.getUtilityBase().printOutput("Called bot command: " + event.getMessage().getContentRaw() + " from: " + event.getGuild().getName() + " from: " + event.getAuthor().getName(), true);
        DiscApplicationUser user = engine.getDiscEngine().getUtilityBase().lookForUserById(event.getAuthor());
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        try {
            engine.getDiscEngine().getCommandHandler().handleServerCommand(engine.getDiscEngine().getCommandParser().parseServerMessage(event.getMessage().getContentRaw(), event, server, user, engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----", true);
            e.printStackTrace();
        }
    }

    private void sendPrivateCommand(PrivateMessageReceivedEvent event) {
        DiscApplicationUser user = engine.getDiscEngine().getUtilityBase().lookForUserById(event.getAuthor());

        try {
            engine.getDiscEngine().getCommandHandler().handlePrivateCommand(engine.getDiscEngine().getCommandParser().parseClientMessage(event.getMessage().getContentRaw(), event, user, engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----", true);
            e.printStackTrace();
        }
    }

    private void pictureSelect(GuildMessageReceivedEvent event) {
        DiscApplicationUser user = engine.getDiscEngine().getUtilityBase().lookForUserById(event.getAuthor());
        String c = event.getMessage().getContentDisplay();
        c = c.substring(1);
        String[] ca = c.split(" ");
        String to = "";
        String pic = "";
        Color color = Color.cyan;
        JSONObject grp = (JSONObject) engine.getPics().get(ca[0]);
        if(ca[0].equals("list")){
            String msg = "";
            for (Object o:engine.getPics().keySet().toArray()) {
                String s = (String) o;
                msg += s + "\n";
            }
            engine.getDiscEngine().getTextUtils().sendCustomMessage(msg, event.getChannel(), "GIF List", Color.blue);
            return;
        }
        if (ca.length < 2) {
            EmbedBuilder b = new EmbedBuilder()
                    .setDescription(engine.lang("func.pic.error.noDest", user.getLang(), new String[]{ca[0]}))
                    .setColor(Color.yellow)
                    .setImage("https://i.kym-cdn.com/photos/images/newsfeed/001/671/387/17c.jpg");
            event.getChannel().sendMessage(b.build()).queue();
            return;
        }
        if (!event.getChannel().isNSFW() && grp.get("nsfw").equals("true")) {
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.nsfwNotAllowedChan", user.getLang(), null), event.getChannel(), false);
            return;
        } else {
            JSONArray pics;
            try {
                pics = (JSONArray) grp.get("arr");
            } catch (Exception e) {
                return;
            }
            pic = (String) pics.get(ThreadLocalRandom.current().nextInt(0, pics.size()));
            to = grp.get("prn") + " " + ca[1];
            try {
                color = Color.decode((String) grp.get("color"));
            } catch (Exception e) {

            }
        }
        EmbedBuilder b = new EmbedBuilder()
                .setColor(color)
                .setImage(pic)
                .setAuthor(event.getAuthor().getName() + " " + to, null, event.getAuthor().getAvatarUrl());

        event.getChannel().sendMessage(b.build()).queue();
    }
}
