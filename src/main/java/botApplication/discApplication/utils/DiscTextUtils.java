package botApplication.discApplication.utils;

import botApplication.discApplication.librarys.DiscApplicationServer;
import core.Engine;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DiscTextUtils {

    static Message msg;

    Engine engine;

    public DiscTextUtils(Engine engine) {
        this.engine = engine;
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     * @param title    shows "error" in the title
     */
    public void sendError(String txt, TextChannel channel, int dellTime, boolean title) {
        if (title) {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).setTitle("Error").build()
            ).complete();
        } else {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).build()
            ).complete();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     * @param title   Title of the message
     * @param color   color of the text
     */
    public void sendCustomMessage(String txt, PrivateChannel channel, String title, Color color) {
        if (color != null) {
            if (title.equals("") || title == null)
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).build()).complete();
            else
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).setTitle(title).build()).complete();
        }
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     * @param title   Title of the message
     * @param color   color of the text
     */
    public void sendCustomMessage(String txt, TextChannel channel, String title, Color color) {
        if (color != null) {
            if (title.equals("") || title == null)
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).build()).complete();
            else
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).setTitle(title).build()).complete();
        }
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param title    Title of the message
     * @param color    color of the text
     * @param dellTime time which is needed to delete this message
     */
    public void sendCustomMessage(String txt, PrivateChannel channel, String title, Color color, int dellTime) {
        if (color != null) {
            if (title.equals("") || title == null)
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).build()).complete();
            else
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).setTitle(title).build()).complete();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param title    Title of the message
     * @param color    color of the text
     * @param dellTime time which is needed to delete this message
     */
    public void sendCustomMessage(String txt, TextChannel channel, String title, Color color, int dellTime) {
        if (color != null) {
            if (title.equals("") || title == null)
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).build()).complete();
            else
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(color).setDescription(txt).setTitle(title).build()).complete();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     * @param title    shows "error" in the title
     */
    public void sendError(String txt, PrivateChannel channel, int dellTime, boolean title) {
        Message msg;
        if (title) {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).setTitle("Error").build()
            ).complete();
        } else {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).build()
            ).complete();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    public void deleteCustomMessage(Message msg, int dellTime) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     * @param title   shows "error" in the title
     */
    public void sendError(String txt, TextChannel channel, boolean title) {
        Message msg;
        if (title) {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).setTitle("Error").build()
            ).complete();
        } else {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).build()
            ).complete();
        }
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     * @param title   shows "error" in the title
     */
    public void sendError(String txt, PrivateChannel channel, boolean title) {
        Message msg;
        if (title) {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).setTitle("Error").build()
            ).complete();
        } else {
            msg = channel.sendMessage(
                    new EmbedBuilder().setColor(Color.RED).setDescription(txt).build()
            ).complete();
        }
    }

    public void sendHelp(String txt, TextChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.BLUE).setTitle("!Hilfe!").setDescription(txt).build()
        ).complete();
    }

    public void sendHelp(String txt, PrivateChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.BLUE).setTitle("!Hilfe!").setDescription(txt).build()
        ).complete();
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendNormalTxt(String txt, TextChannel channel, int dellTime) {
        Message msg = channel.sendMessage(txt).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendNormalTxt(String txt, PrivateChannel channel, int dellTime) {
        Message msg = channel.sendMessage(txt).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendNormalTxt(String txt, TextChannel channel) {
        Message msg = channel.sendMessage(txt).complete();
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendNormalTxt(String txt, PrivateChannel channel) {
        Message msg = channel.sendMessage(txt).complete();
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendWarining(String txt, TextChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.YELLOW).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendWarining(String txt, PrivateChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.YELLOW).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendWarining(String txt, TextChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.YELLOW).setDescription(txt).build()
        ).complete();
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendWarining(String txt, PrivateChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.YELLOW).setDescription(txt).build()
        ).complete();
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendSucces(String txt, TextChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     */
    public void sendSucces(String txt, PrivateChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendSucces(String txt, TextChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription(txt).build()
        ).complete();
    }

    /**
     * outdated!
     *
     * @param txt     Text which will be send
     * @param channel Text channel destination
     */
    public void sendSucces(String txt, PrivateChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GREEN).setDescription(txt).build()
        ).complete();
    }

    /**
     * @param txt      Text which will be send
     * @param channel  Text channel destination
     * @param dellTime time which is needed to delete this message
     * @param songName name of the song
     * @param server   server props
     */
    public void sendNewMusicInfo(String txt, TextChannel channel, int dellTime, String songName, DiscApplicationServer server) {
        if (server.isMusicListenerEnabled()) {
            Message msg;
            String picId;
            if (txt.startsWith("http:")) {
                picId = txt.substring(31);
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(Color.GREEN).setDescription(":musical_note:**Es spielt:** \n`" + songName + "`").setImage("https://img.youtube.com/vi/" + picId + "/sddefault.jpg").build()
                ).complete();
            } else if (txt.startsWith("https:")) {
                picId = txt.substring(32);
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(Color.GREEN).setDescription(":musical_note:**Es spielt:** \n`" + songName + "`").setImage("http://img.youtube.com/vi/" + picId + "/sddefault.jpg").build()
                ).complete();
            } else {
                msg = channel.sendMessage(
                        new EmbedBuilder().setColor(Color.GREEN).setDescription(txt).build()
                ).complete();
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, dellTime);
        }
    }

    public void sendWellcomeMessage(String txt, TextChannel channel) {
        EmbedBuilder help = new EmbedBuilder().setColor(Color.CYAN).setTitle("**!Hello!**");
        channel.sendMessage(help.setDescription(txt).build()).queue();
    }

    public void sendShutdownMessage(String txt, TextChannel channel) {
        EmbedBuilder help = new EmbedBuilder().setColor(Color.CYAN).setTitle("**!Bye!**");
        channel.sendMessage(help.setDescription(txt).build()).queue();
    }

    public void deletUserMessage(int size, GuildMessageReceivedEvent event) {
        MessageHistory history = new MessageHistory(event.getChannel());
        List<Message> msgs;

        event.getMessage().delete().queue();

        msgs = history.retrievePast(size).complete();
        try {
            event.getChannel().deleteMessages(msgs).queue();
        } catch (Exception e) {
        }
    }

    public void sendChannelConsolMessage(String txt, TextChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GRAY).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    public void sendChannelConsolMessage(String txt, PrivateChannel channel, int dellTime) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GRAY).setDescription(txt).build()
        ).complete();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, dellTime);
    }

    public void sendChannelConsolMessage(String txt, PrivateChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GRAY).setDescription(txt).build()
        ).complete();
    }

    public void sendChannelConsolMessage(String txt, TextChannel channel) {
        Message msg;
        msg = channel.sendMessage(
                new EmbedBuilder().setColor(Color.GRAY).setDescription(txt).build()
        ).complete();
    }

    public void sendSysOutFromServerMessage(Guild g, String text) {
        engine.getUtilityBase().printOutput(g.getId() + " -- " + g.getName() + " Sysout: " + text, false);
    }

    public void sendSysOutFromBotMessage(String className, String text) {
        engine.getUtilityBase().printOutput("[" + className + "]" + " " + text, false);
    }

    /**
     * @param raw     (color title text)
     * @param channel
     */
    public void createCustomMassage(String raw, PrivateChannel channel) {
        String[] split = raw.split(" ");
        String txt = "";
        for (int i = 2; split.length > i; i++) {
            txt = txt + split[i];
            txt = txt + " ";
        }
        try {
            sendCustomMessage(txt, channel, split[1], engine.getUtilityBase().convertStringToColor(split[0]));
        } catch (Exception e) {
            sendError("Diese Farbe existiert nicht!", channel, true);
        }
    }

    /**
     * @param raw      (color title text)
     * @param channel  the channel to send
     * @param dellTime
     */
    public void createCustomMassage(String raw, PrivateChannel channel, PrivateChannel own, int dellTime) {
        String[] split = raw.split(" ");
        String txt = "";
        for (int i = 2; split.length > i; i++) {
            txt = txt + split[i];
            txt = txt + " ";
        }
        try {
            sendCustomMessage(txt, channel, split[1], engine.getUtilityBase().convertStringToColor(split[0]), dellTime);
        } catch (Exception e) {
            sendError("Diese Farbe existiert nicht!", channel, true);
        }
    }

    public void createCustomMassage(String raw, TextChannel channel, PrivateChannel own, int dellTime) {
        String[] split = raw.split(" ");
        String txt = "";
        for (int i = 2; split.length > i; i++) {
            txt = txt + split[i];
            txt = txt + " ";
        }
        try {
            sendCustomMessage(txt, channel, split[1], engine.getUtilityBase().convertStringToColor(split[0]), dellTime);
        } catch (Exception e) {
            sendError("Diese Farbe existiert nicht!", channel, true);
        }
    }

    public void createCustomMassage(String raw, TextChannel channel, PrivateChannel own) {
        String[] split = raw.split(" ");
        String txt = "";
        for (int i = 2; split.length > i; i++) {
            txt = txt + split[i];
            txt = txt + " ";
        }
        try {
            sendCustomMessage(txt, channel, split[1], engine.getUtilityBase().convertStringToColor(split[0]));
        } catch (Exception e) {
            sendError("Diese Farbe existiert nicht!", channel, true);
        }
    }
}
