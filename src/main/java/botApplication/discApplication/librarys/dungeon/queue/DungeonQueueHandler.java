package botApplication.discApplication.librarys.dungeon.queue;

import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class DungeonQueueHandler implements Serializable {

    private static final long serialVersionUID = 42L;

    private String msgId = "";
    private String emoji = "";
    private ArrayList<DungeonChannelHandler> channels = new ArrayList<>();
    private transient HashMap<String, UsedDungeonChannel> usedChannels = new HashMap<>();

    public void click(TextChannel textChannel, Engine engine, Guild g, Member member) {
        DiscApplicationUser user = engine.getDiscEngine().getFilesHandler().getUserById(member.getId());

        Instant fourHoursAgo = Instant.now().minus(Duration.ofHours(4));
        Date dFourHoursAgo = Date.from(fourHoursAgo);

        if(user.getLastDungeonVisit() != null)
        if (user.getLastDungeonVisit().after(dFourHoursAgo)) {
            String diff = engine.getUtilityBase().convertTimeToString(user.getLastDungeonVisit().getTime() - dFourHoursAgo.getTime());
            EmbedBuilder b = new EmbedBuilder().setColor(Color.RED).setDescription("You can visit the dungeon in " + diff + " hours again!");
            Message m = textChannel.sendMessage(b.build()).complete();
            m.delete().queueAfter(8, TimeUnit.SECONDS);
            return;
        }

        if (usedChannels == null)
            usedChannels = new HashMap<>();

        DungeonChannelHandler channelHandler = null;
        for (DungeonChannelHandler ch : channels) {
            if (!usedChannels.containsKey(ch.getChannelId())) {
                channelHandler = ch;
                break;
            }
        }
        if (channelHandler == null) {
            EmbedBuilder b = new EmbedBuilder().setColor(Color.RED).setDescription("There is no dungeon channel left sorry, wait 3 minutes and try again!");
            Message m = textChannel.sendMessage(b.build()).complete();
            m.delete().queueAfter(5, TimeUnit.SECONDS);
        } else {
            channelHandler.clicked(engine, g, member);
            UsedDungeonChannel chanel = new UsedDungeonChannel(channelHandler, member, g);
            usedChannels.put(channelHandler.getChannelId(), chanel);
            user.setLastDungeonVisit(new Date());
        }
    }

    public void unuseChannel(String id, Guild g) {
        Iterator<UsedDungeonChannel> it = usedChannels.values().iterator();
        while (it.hasNext()) {
            UsedDungeonChannel ch = it.next();
            if (ch.getCh().getChannelId().equals(id)) {
                usedChannels.remove(ch.getCh().getChannelId());
                TextChannel tc = g.getTextChannelById(ch.getCh().getChannelId());
                try {
                    tc.deleteMessages(tc.getIterableHistory().complete()).queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ch.getG().removeRoleFromMember(ch.getM(), ch.getG().getRoleById(ch.getCh().getRoleId())).queue();
                break;
            }
        }
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public ArrayList<DungeonChannelHandler> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<DungeonChannelHandler> channels) {
        this.channels = channels;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
