package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DiscMessageListener extends ListenerAdapter {

    private final Engine engine;

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
            engine.getUtilityBase().printOutput(" message listener received guild message!", true);
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

            if(event.getMessage().getContentRaw().startsWith("!")){
                sendSpecialCommand(event);
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
            engine.getUtilityBase().printOutput(" message listener received private message!", true);
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
        DiscApplicationUser user = DiscUtilityBase.lookForUserById(event.getAuthor(), engine);
        DiscApplicationServer server = DiscUtilityBase.lookForServer(event.getGuild(), engine);
        try {
            engine.getDiscEngine().getCommandHandler().handleServerCommand(engine.getDiscEngine().getCommandParser().parseServerMessage(event.getMessage().getContentRaw(), event, server, user, engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----", true);
            e.printStackTrace();
        }
    }

    private void sendPrivateCommand(PrivateMessageReceivedEvent event) {
        DiscApplicationUser user = DiscUtilityBase.lookForUserById(event.getAuthor(), engine);

        try {
            engine.getDiscEngine().getCommandHandler().handlePrivateCommand(engine.getDiscEngine().getCommandParser().parseClientMessage(event.getMessage().getContentRaw(), event, user, engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----", true);
            e.printStackTrace();
        }
    }

    private void pictureSelect(GuildMessageReceivedEvent event) {
        DiscApplicationUser user = DiscUtilityBase.lookForUserById(event.getAuthor(), engine);
        String c = event.getMessage().getContentDisplay();
        c = c.substring(1);
        String[] ca = c.split(" ");
        String to = "";
        String pic = "";
        Color color = Color.cyan;
        JSONObject grp = (JSONObject) engine.getPics().get(ca[0]);
        if (ca[0].equals("list")) {
            String msg = "";
            for (Object o : engine.getPics().keySet().toArray()) {
                String s = (String) o;
                msg += s + "\n";
            }
            engine.getDiscEngine().getTextUtils().sendCustomMessage(msg, event.getChannel(), "GIF List", Color.blue);
            return;
        }
        if (ca.length < 2 && grp != null) {
            EmbedBuilder b = new EmbedBuilder()
                    .setDescription(engine.lang("func.pic.error.noDest", user.getLang(), new String[]{ca[0]}))
                    .setColor(Color.yellow)
                    .setImage("https://i.kym-cdn.com/photos/images/newsfeed/001/671/387/17c.jpg");
            event.getChannel().sendMessage(b.build()).queue();
            return;
        }
        if (grp == null)
            return;

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
            pic = (String) pics.get(ThreadLocalRandom.current().nextInt(0, pics.size() - 1));
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

    private void sendSpecialCommand(GuildMessageReceivedEvent e){
        DiscApplicationServer server = DiscUtilityBase.lookForServer(e.getGuild(), engine);
        DiscApplicationUser user = DiscUtilityBase.lookForUserById(e.getAuthor(), engine);

        if(!user.isBooster())
            return;

        String[] inv1 = e.getMessage().getContentRaw().split(" ");
        String invoke = inv1[0].substring(1);
        List<Member> mem = e.getMessage().getMentionedMembers();
        e.getMessage().getMentionedRoles().forEach(a -> mem.addAll(a.getGuild().getMembersWithRoles(a)));
        TextChannel tc = getTc(e, user);
        VoiceChannel vc = null;
        String args1 = "";

        if(inv1.length > 1)
            for (int i = 1; i < inv1.length; i++) {
                args1 += inv1[i];
                if(i + 1 != inv1.length)
                    args1 += " ";
            }

        if(tc == null)
            vc = getVc(e, user);

        if((vc == null && tc == null) || (vc != null && tc != null)){
            engine.getDiscEngine().getTextUtils().sendError("No channel was found, please try to specify which channel you mean.", e.getChannel(), false);
            return;
        }

        switch (invoke){
            case "inv":
                if(tc != null){
                    for (Member m:mem) {
                        tc.createPermissionOverride(m).setAllow(Permission.ALL_TEXT_PERMISSIONS).queue();
                    }
                } else if(vc != null){
                    for (Member m:mem) {
                        vc.createPermissionOverride(m).setAllow(Permission.ALL_VOICE_PERMISSIONS).queue();
                    }
                }
                break;

            case "rem":
                if(tc != null){
                    for (Member m:mem) {
                        tc.getManager().removePermissionOverride(m).queue();
                    }
                } else if(vc != null){
                    for (Member m:mem) {
                        vc.getManager().removePermissionOverride(m).queue();
                    }
                }
                break;

            case "vc":
                if(tc != null){
                    for (String s:user.getBoosterChans()) {
                        if(tc.getId().equals(s)){
                            tc.delete().queue();
                            user.getBoosterChans().remove(s);
                            user.setEdit(true);

                            VoiceChannel v = e.getGuild().createVoiceChannel(user.getUserName(), e.getGuild().getCategoryById(server.getBoosterCategoryId())).complete();
                            user.addBoosterChan(v.getId());
                            break;
                        }
                    }
                } else if(vc != null){
                    engine.getDiscEngine().getTextUtils().sendError("This channel is a Voice Channel already!", e.getChannel(), false);
                    return;
                }
                break;

            case "tc":
                if(tc != null){
                    engine.getDiscEngine().getTextUtils().sendError("This channel is a Voice Channel already!", e.getChannel(), false);
                    return;
                } else if(vc != null){
                    for (String s:user.getBoosterChans()) {
                        if(vc.getId().equals(s)){
                            vc.delete().queue();
                            user.getBoosterChans().remove(s);
                            user.setEdit(true);

                            TextChannel v = e.getGuild().createTextChannel(user.getUserName(), e.getGuild().getCategoryById(server.getBoosterCategoryId())).complete();
                            user.addBoosterChan(v.getId());
                            break;
                        }
                    }
                }
                break;

            case "name":
                if(tc != null){
                    try {
                        tc.getManager().setName(args1).queue();
                    } catch (Exception er){
                       engine.getDiscEngine().getTextUtils().sendError("This name is invalid!", e.getChannel(), false);
                       return;
                    }
                } else if (vc != null){
                    try {
                        vc.getManager().setName(args1).queue();
                    } catch (Exception er){
                        engine.getDiscEngine().getTextUtils().sendError("This name is invalid!", e.getChannel(), false);
                        return;
                    }
                }
                break;
        }
        engine.getDiscEngine().getTextUtils().sendSucces("Updated your channel!", e.getChannel());
    }

    private TextChannel getTc(GuildMessageReceivedEvent event, DiscApplicationUser user){
        TextChannel tc = null;

        if(user.getBoosterChans().size() == 1){
            tc = event.getGuild().getTextChannelById(user.getBoosterChans().get(0));
        }
        if(tc == null)
        try {
            tc = event.getMessage().getMentionedChannels().get(0);
        } catch (Exception e){
        }

        return tc;
    }

    private VoiceChannel getVc(GuildMessageReceivedEvent event, DiscApplicationUser user){
        VoiceChannel vc = null;
        if(user.getBoosterChans().size() == 1){
            vc = event.getGuild().getVoiceChannelById(user.getBoosterChans().get(0));
        }
        return vc;
    }
}
