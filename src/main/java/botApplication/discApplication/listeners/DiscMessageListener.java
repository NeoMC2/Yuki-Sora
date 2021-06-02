package botApplication.discApplication.listeners;

import botApplication.discApplication.commands.DiscCmdContest;
import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DiscMessageListener extends ListenerAdapter {

    private final Engine engine;

    private final Permission[] textPermissions = {Permission.MESSAGE_READ, Permission.VIEW_CHANNEL, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS};
    private final Permission[] voicePermissions = {Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_USE_VAD};

    private final HashMap<String, WatchedUser> watchedUserStringHashMap = new HashMap<>();

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
            //Response
            if (engine.getResponseHandler().lookForResponse(event)) {
                return;
            }

            //Contest
            DiscCmdContest.Contest contest = engine.getDiscEngine().getContestCmd().checkChannel(engine, event.getChannel());

            if (contest != null) {
                if(contest.isParticipant(event.getAuthor().getId()))
                    event.getMessage().delete().queue();
                else {
                    if(event.getMessage().getAttachments().size() == 0 || !contest.isOpen())
                        event.getMessage().delete().queue();
                    else {
                        event.getMessage().addReaction("\u2705").queue();
                        contest.addUserParticipate(event.getAuthor().getId());
                    }
                }
            }



            if (event.getMessage().getContentRaw().startsWith("?tp") || event.getMessage().getContentRaw().startsWith("?topic")) {
                //test command ban
                if(testWatch(event.getMember(), event.getChannel())){
                    event.getMessage().delete().queue();
                    return;
                }

                try {
                    sendTopic(event);
                } catch (Exception e) {
                }
                return;
            }

            //Gifs
            if (event.getMessage().getContentRaw().startsWith(".")) {
                try {
                    pictureSelect(event);
                } catch (Exception e) {
                }
                return;
            }

            //Specific VIP commands
            if (event.getMessage().getContentRaw().startsWith("!")) {
                //test command ban
                if(testWatch(event.getMember(), event.getChannel())){
                    event.getMessage().delete().queue();
                    return;
                }

                sendVIPCommand(event);
                return;
            }

            //Command test
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
                            //test command ban
                            if(testWatch(event.getMember(), event.getChannel())){
                                event.getMessage().delete().queue();
                                return;
                            }

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
                return;
            }

            handleSecret(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().length() > 400) {
            return;
        }
        boolean commandWorked = false;

        //Specific commands
        if (event.getMessage().getContentRaw().startsWith("!")) {
            sendSpecificPrivateCommand(event.getMessage().getContentRaw(), event.getChannel(), event.getAuthor());
            return;
        }

        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            //Response
            if (engine.getResponseHandler().lookForResponse(event)) {
                return;
            }
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
                    engine.getDiscEngine().getTextUtils().sendError("Command " + event.getMessage().getContentRaw() + "  does not exist!\n\nWrite **" + engine.getProperties().discBotApplicationPrefix + "help** to get a general overview of the commands.", event.getChannel(), engine.getProperties().middleTime, true);
                }
            }
        }
    }

    private boolean testWatch(Member m, TextChannel tc){
        if(watchedUserStringHashMap.containsKey(m.getId())){
            WatchedUser user = watchedUserStringHashMap.get(m.getId());
            if(user.testMessage()){
                engine.getDiscEngine().getTextUtils().sendWarining(":no_entry_sign: You are currently banned from sending commands to the bot! :no_entry_sign:\n\nTime banned: `" + user.getTimeBanned() + "`",tc, 10*10*10*6);
                return true;
            }
        } else {
            WatchedUser user = new WatchedUser();
            user.testMessage();
            watchedUserStringHashMap.put(m.getId(), user);
        }
        return false;
    }

    private void sendSpecificPrivateCommand(String cmd, PrivateChannel channel, User user) {
        cmd = cmd.substring(1);
        String[] cmds = cmd.split(" ");
        switch (cmds[0]) {
            case "rename":
            case "rn":
                renameAutoChannel(cmds, user, null, channel);
                break;
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
                    case "manga":
                        sendNormalText(event, "Who said Manga :O");
                        return;
                    case "anime":
                        sendNormalText(event, "Who said Anime :O");
                        return;
                    case "baka":
                        sendNormalText(event, "**Baaaaaaaaaaaaaka!** XD");
                        return;
                    case "aloha":
                        sendNormalText(event, "Aloha " + event.getAuthor().getName() + " XD \nDas hat mir der Mosel beigebracht ");
                        return;
                }
            }

            switch (message.toLowerCase()) {
                case "was liebt hanna":
                case "was mag hanna":
                case "was sucht hanna":
                case "was will hanna":
                case "was möchte hanna":
                case "was isst hanna":
                case "was braucht hanna":
                case "was holt hanna":
                    sendNormalText(event, ":cookie: :cookie: **!KEKSE!** :cookie: :cookie:");
                    return;

                case "ich will hanna":
                    sendNormalText(event, "HAHAHAHAHAHAHAHAHAHAHAHAHAHA\n\n\nNo");
                    return;
            }

            if (event.getAuthor().getId().equals("510872452654694410")) {
                if (event.getMessage().getContentRaw().equals("was will ich"))
                    sendNormalText(event, ":cookie: :cookie: **!KEKSE!** :cookie: :cookie:");
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

    private void sendTopic(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] msgs = msg.split(" ");
        String search = "";
        for (int i = 1; i < msgs.length; i++) {
            search += msgs[i] + " ";
        }
        search = search.substring(0, search.length() - 1);
        JSONObject res = (JSONObject) engine.getDiscEngine().getApiManager().getRandomTopic(search, event.getChannel().isNSFW()).get("data");
        String title = (String) res.get("topic");
        String des = (String) res.get("description");
        EmbedBuilder b = new EmbedBuilder().setColor(Color.ORANGE).setAuthor(title).setDescription(des);
        event.getChannel().sendMessage(b.build()).queue();
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
                    .setDescription(engine.lang("func.pic.error.noDest", user.getLang(), ca))
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
            String memberTo = "";
            for (Member m : event.getMessage().getMentionedMembers()) {
                memberTo += m.getNickname() + " ";
            }
            if (memberTo.equals(""))
                for (int i = 1; i < ca.length; i++) {
                    memberTo += ca[i] + " ";
                }
            to = grp.get("prn") + " " + memberTo;
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

    private void sendVIPCommand(GuildMessageReceivedEvent e) {
        DiscApplicationServer server = DiscUtilityBase.lookForServer(e.getGuild(), engine);
        DiscApplicationUser user = DiscUtilityBase.lookForUserById(e.getAuthor(), engine);

        String[] inv1 = e.getMessage().getContentRaw().split(" ");
        String invoke = inv1[0].substring(1);
        boolean done = false;

        switch (invoke) {
            case "rn":
            case "rename":
                renameAutoChannel(inv1, e.getAuthor(), e.getChannel(), null);
                done = true;
                break;
        }

        if (done)
            return;

        if (!user.isBooster())
            return;

        List<Member> mem = e.getMessage().getMentionedMembers();
        e.getMessage().getMentionedRoles().forEach(a -> mem.addAll(a.getGuild().getMembersWithRoles(a)));
        TextChannel tc = getTc(e, user);
        VoiceChannel vc = null;
        String args1 = "";

        if (inv1.length > 1)
            for (int i = 1; i < inv1.length; i++) {
                args1 += inv1[i];
                if (i + 1 != inv1.length)
                    args1 += " ";
            }

        if (tc == null)
            vc = getVc(e, user);

        if ((vc == null && tc == null) || (vc != null && tc != null)) {
            engine.getDiscEngine().getTextUtils().sendError("No channel was found, please try to specify which channel you mean.", e.getChannel(), false);
            return;
        }

        switch (invoke) {
            case "inv":
                if (!user.isBooster())
                    return;
                if (tc != null) {
                    for (Member m : mem) {
                        tc.createPermissionOverride(m).setAllow(textPermissions).queue();
                    }
                } else if (vc != null) {
                    for (Member m : mem) {
                        vc.createPermissionOverride(m).setAllow(voicePermissions).queue();
                    }
                }
                break;

            case "rem":
                if (tc != null) {
                    for (Member m : mem) {
                        tc.getManager().removePermissionOverride(m).queue();
                    }
                } else if (vc != null) {
                    for (Member m : mem) {
                        vc.getManager().removePermissionOverride(m).queue();
                    }
                }
                break;

            case "vc":
                if (tc != null) {
                    for (String s : user.getBoosterChans()) {
                        if (tc.getId().equals(s)) {
                            VoiceChannel v;

                            try {
                                v = e.getGuild().createVoiceChannel(tc.getName(), e.getGuild().getCategoryById(server.getBoosterCategoryId())).complete();
                            } catch (Exception er) {
                                engine.getDiscEngine().getTextUtils().sendError("Can't change channel!", e.getChannel(), false);
                                if (engine.getProperties().debug)
                                    er.printStackTrace();
                                return;
                            }

                            user.getBoosterChans().remove(s);
                            user.setEdit(true);
                            user.addBoosterChan(v.getId());

                            for (PermissionOverride po : tc.getMemberPermissionOverrides()) {
                                try {
                                    v.putPermissionOverride(po.getMember()).setAllow(voicePermissions).setDeny(po.getDenied()).complete();
                                } catch (Exception er) {
                                    if (engine.getProperties().debug)
                                        er.printStackTrace();
                                }
                            }
                            tc.delete().queue();
                            break;
                        }
                    }
                } else if (vc != null) {
                    engine.getDiscEngine().getTextUtils().sendError("This channel is a Voice Channel already!", e.getChannel(), false);
                    return;
                }
                break;

            case "tc":
                if (tc != null) {
                    engine.getDiscEngine().getTextUtils().sendError("This channel is a Voice Channel already!", e.getChannel(), false);
                    return;
                } else if (vc != null) {
                    for (String s : user.getBoosterChans()) {
                        if (vc.getId().equals(s)) {
                            TextChannel v;
                            try {
                                v = e.getGuild().createTextChannel(vc.getName(), e.getGuild().getCategoryById(server.getBoosterCategoryId())).complete();
                            } catch (Exception er) {
                                engine.getDiscEngine().getTextUtils().sendError("Can't change channel!", e.getChannel(), false);
                                if (engine.getProperties().debug)
                                    er.printStackTrace();
                                return;
                            }

                            user.getBoosterChans().remove(s);
                            user.setEdit(true);
                            user.addBoosterChan(v.getId());

                            for (PermissionOverride po : vc.getMemberPermissionOverrides()) {
                                try {
                                    v.putPermissionOverride(po.getMember()).setAllow(textPermissions).complete();
                                } catch (Exception er) {
                                    if (engine.getProperties().debug)
                                        er.printStackTrace();
                                }
                            }
                            vc.delete().queue();
                            break;
                        }
                    }
                }
                break;

            case "name":
                if (tc != null) {
                    try {
                        tc.getManager().setName(args1).queue();
                    } catch (Exception er) {
                        engine.getDiscEngine().getTextUtils().sendError("This name is invalid!", e.getChannel(), false);
                        return;
                    }
                } else if (vc != null) {
                    try {
                        vc.getManager().setName(args1).queue();
                    } catch (Exception er) {
                        engine.getDiscEngine().getTextUtils().sendError("This name is invalid!", e.getChannel(), false);
                        return;
                    }
                }
                break;
        }
        try {
            engine.getDiscEngine().getTextUtils().sendSucces("Updated your channel!", e.getChannel());
        } catch (Exception er) {
        }
    }

    private void renameAutoChannel(String[] args, User user, TextChannel tc, PrivateChannel pc) {
        String newName = "";
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                newName += args[i] + " ";
            }
            newName = newName.substring(0, newName.length() - 1);
        } else {
            if (tc == null)
                engine.getDiscEngine().getTextUtils().sendError("You have to give a name for the channel!", pc, false);
            else
                engine.getDiscEngine().getTextUtils().sendError("You have to give a name for the channel!", tc, false);
            return;
        }
        if (engine.getDiscEngine().discVoiceListener.renameAutoChannelByUser(user, newName))
            if (tc == null)
                engine.getDiscEngine().getTextUtils().sendSucces("The channel was renamed to `" + newName + "`", pc);
            else
                engine.getDiscEngine().getTextUtils().sendSucces("The channel was renamed to `" + newName + "`", tc);
        else if (tc == null)
            engine.getDiscEngine().getTextUtils().sendError("You are not in a valid autochannel!", pc, false);
        else
            engine.getDiscEngine().getTextUtils().sendError("You are not in a valid autochannel!", tc, false);
    }

    private TextChannel getTc(GuildMessageReceivedEvent event, DiscApplicationUser user) {
        TextChannel tc = null;

        if (user.getBoosterChans().size() == 1) {
            tc = event.getGuild().getTextChannelById(user.getBoosterChans().get(0));
        }
        if (tc == null)
            try {
                tc = event.getMessage().getMentionedChannels().get(0);
            } catch (Exception e) {
            }

        return tc;
    }

    private VoiceChannel getVc(GuildMessageReceivedEvent event, DiscApplicationUser user) {
        VoiceChannel vc = null;
        if (user.getBoosterChans().size() == 1) {
            vc = event.getGuild().getVoiceChannelById(user.getBoosterChans().get(0));
        }
        return vc;
    }

    private class WatchedUser {

        private int msgs;

        // 0 = unauffällig, 1 = 1 min ban,2 = 5 min ban, 3 = 4 Stunden ban, 4 = 24 h ban
        private int level;
        private Date bannedUntil;
        private Date lastWrite;

        public boolean testMessage(){
            if(bannedUntil != null){
                Date now = new Date();
                if(now.after(bannedUntil)){
                    msgs = 0;
                    bannedUntil = null;
                } else {
                    return true;
                }
            }

            boolean ban = false;
            if(lastWrite != null) {
                if(Instant.now().isBefore(lastWrite.toInstant().plusSeconds(5))){
                    msgs++;
                    if(msgs > 3){
                        ban = true;
                        ban();
                    }
                } else {
                    msgs = 0;
                }
            }

            lastWrite = new Date();
            return ban;
        }

        public void ban() {
            lastWrite = null;
            msgs = 0;

            if (level == 0) {
                level = 1;
            } else if (level == 1) {
                level = 2;
            } else if (level == 2) {
                level = 3;
            } else if (level == 3) {
                level = 4;
            }

            if (level == 1) {
                setBannedUntil(60);
            } else if (level == 2) {
                setBannedUntil(60*5);
            } else if (level == 3) {
                setBannedUntil(60*60*4);
            } else if(level == 4){
                setBannedUntil(60*60*24);
            }
        }

        private void setBannedUntil(int seconds){
            Date d = new Date();
            Instant i = d.toInstant();
            i = i.plusSeconds(seconds);
            bannedUntil = Date.from(i);
        }

        public String getTimeBanned(){
            if(bannedUntil == null){
                return "no time found";
            }
            Instant now = Instant.now();
            Instant event = bannedUntil.toInstant();

            Duration diff = Duration.between(now, event);
            long hours = diff.toHours();
            long minutes = diff.toMinutes() - hours * 60;
            long seconds = diff.getSeconds() - minutes * 60;

            return hours + ":" + minutes + ":" + seconds;
        }

        @Override
        public String toString() {
            return "WatchedUser{" +
                    "msgs=" + msgs +
                    ", level=" + level +
                    ", bannedUntil=" + bannedUntil +
                    ", lastWrite=" + lastWrite +
                    '}';
        }
    }
}
