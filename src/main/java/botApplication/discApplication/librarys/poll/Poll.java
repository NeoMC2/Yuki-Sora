package botApplication.discApplication.librarys.poll;

import botApplication.discApplication.commands.DiscCmdVote;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Poll implements Serializable {

    private static final long serialVersionUID = 42L;
    DiscCmdVote.PollType pollType = DiscCmdVote.PollType.Vote;
    private String guildId = "";
    private String creator;
    private String heading;
    private String messageId;
    private ArrayList<PollAnswer> answers = new ArrayList<>();
    private String channel;
    private Color color = Color.cyan;

    public Poll(String guildId) {
        this.guildId = guildId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public ArrayList<PollAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<PollAnswer> answers) {
        this.answers = answers;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DiscCmdVote.PollType getPollType() {
        return pollType;
    }

    public void setPollType(DiscCmdVote.PollType pollType) {
        this.pollType = pollType;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public Message update(String answer, int quant, Guild g, Member voter, Message message, Engine engine) {
        TextChannel t = g.getTextChannelById(channel);
        Message msg;
        EmbedBuilder ans = null;
        if (pollType == DiscCmdVote.PollType.Vote) {
            String des = "";
            for (int i = 0; i < answers.size(); i++) {
                PollAnswer pa = answers.get(i);
                if (answer.equals(pa.getAnswerEmoji())) {
                    if (message == null)
                        pa.setCount(0);
                    else {
                        for (MessageReaction mr : message.getReactions()) {
                            if (mr.getReactionEmote().getName().equals(pa.getAnswerEmoji())) {
                                pa.setCount(mr.getCount() - 1);
                                break;
                            }
                        }
                    }

                }

                if(pa.isEmojiServerEmote()) {
                    des += getEmoteString(g, pa.getAnswerEmoji()) + " " + pa.getAnswer() + " ```Votes:" + pa.getCount() + "```" + "\n";
                }
                    else
                des += pa.getAnswerEmoji() + " " + pa.getAnswer() + " ```Votes:" + pa.getCount() + "```" + "\n";
            }
            ans = new EmbedBuilder()
                    .setAuthor(heading, null, null)
                    .setColor(color)
                    .setDescription(des);
        } else if (pollType == DiscCmdVote.PollType.UserProperty) {
            String des = "";
            Role r = null;
            for (int i = 0; i < answers.size(); i++) {
                PollAnswer pa = answers.get(i);
                if(pa.isEmojiServerEmote())
                    des = des + getEmoteString(g, pa.getAnswerEmoji()) + " " + pa.getAnswer() + "\n";
                    else
                des = des + pa.getAnswerEmoji() + " " + pa.getAnswer() + "\n";
                if (answer.equals(pa.getAnswerEmoji())) {
                    r = g.getRoleById(pa.getRole());
                }
            }
            ans = new EmbedBuilder()
                    .setAuthor(heading, null, null)
                    .setColor(color)
                    .setDescription(des);
            try {
                if (quant == 1) {
                    g.addRoleToMember(voter, r).queue();
                } else if (quant == -1) {
                    g.removeRoleFromMember(voter, r).queue();
                }
            } catch (Exception e) {
                engine.getUtilityBase().printOutput("[Vote Update] !!!Cant add or remove role from member!!!", true);
            }

        } else if (pollType == DiscCmdVote.PollType.Lang) {
            DiscApplicationUser usr = null;
            if (voter != null) {
                try {
                    usr = engine.getDiscEngine().getFilesHandler().getUserById(voter.getUser().getId());
                } catch (Exception e) {
                    engine.getDiscEngine().getFilesHandler().createNewUser(voter.getUser(), DiscCertificationLevel.Member);
                }
            }
            String des = "";
            String lang = "";
            for (int i = 0; i < answers.size(); i++) {
                PollAnswer pa = answers.get(i);
                des = des + pa.getAnswerEmoji() + " " + pa.getAnswer() + "\n";
                if (answer.equals(pa.getAnswerEmoji())) {
                    lang = pa.getLang();
                }
            }
            ans = new EmbedBuilder()
                    .setAuthor(heading, null, null)
                    .setColor(color)
                    .setDescription(des);
            try {
                if (quant == 1) {
                    usr.setLang(lang);
                } else if (quant == -1) {
                    usr.setLang("");
                }
            } catch (Exception e) {
                engine.getUtilityBase().printOutput("[Vote Update] !!!Cant add or remove language from user!!!", true);
            }
        }
        if (quant > -2)
            msg = t.editMessageById(messageId, ans.build()).complete();
        else
            msg = t.sendMessage(ans.build()).complete();

        return msg;
    }

    public void addAnswer(PollAnswer pa) {
        answers.add(pa);
    }

    public boolean create(Guild g, Engine engine, TextChannel command) {
        TextChannel t;
        try {
            t = g.getTextChannelById(channel);
        } catch (Exception e){
            engine.getDiscEngine().getTextUtils().sendError("The channel wasn't found", command, true);
            return false;
        }
        Message msg = update("0x33333", -1000, g, null, null, engine);
        for (PollAnswer pa : answers) {
            try {
                if (pa.isEmojiServerEmote()) {
                    List<Emote> es = g.getEmotesByName(pa.getAnswerEmoji().replace(":", ""), false);
                    if (es.size() > 0)
                        t.addReactionById(msg.getId(), es.get(0)).complete();
                } else
                    t.addReactionById(msg.getId(), pa.getAnswerEmoji()).complete();
            } catch (Exception e) {
                engine.getDiscEngine().getTextUtils().sendError("Error while reating with emoji: " + pa.getAnswerEmoji(), command, true);
                return false;
            }
        }
        messageId = msg.getId();
        return true;
    }

    private String getEmoteString(Guild g, String emote){
        Emote em = g.getEmotesByName(emote.replace(":", ""), false).get(0);

        return em.getAsMention();
    }
}