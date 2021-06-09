package botApplication.discApplication.librarys;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Contest implements Serializable {

    public static final long serialVersionUID = 42L;

    private String tc;
    private String guild;
    private int reward;
    private ArrayList<String> usersParticipate = new ArrayList<>();
    private ArrayList<String> userVoted = new ArrayList<>();
    private Date contestEnd;
    private boolean isOpen = true;

    public Contest(TextChannel tc, int reward, Date contestEnd) {
        this.tc = tc.getId();
        this.guild = tc.getGuild().getId();
        this.reward = reward;
        this.contestEnd = contestEnd;
    }

    public boolean isParticipant(String user) {
        for (String s : usersParticipate) {
            if (user.equals(s))
                return true;
        }
        return false;
    }

    public boolean isVoter(String user) {
        for (String s : userVoted) {
            if (user.equals(s))
                return true;
        }
        return false;
    }

    public void addUserParticipate(String user) {
        usersParticipate.add(user);
    }

    public void addUserVoted(String user) {
        userVoted.add(user);
    }

    public void removeUserVoted(String user) {
        userVoted.remove(user);
    }

    public void removeUserParticipate(String user) {
        usersParticipate.remove(user);
    }

    public TextChannel getTextChannel(Guild g) {
        return g.getTextChannelById(tc);
    }

    public TextChannel getTextChannel(JDA jda) {
        return getGuild(jda).getTextChannelById(tc);
    }

    public Guild getGuild(JDA jda) {
        return jda.getGuildById(guild);
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public ArrayList<String> getUsersParticipate() {
        return usersParticipate;
    }

    public void setUsersParticipate(ArrayList<String> usersParticipate) {
        this.usersParticipate = usersParticipate;
    }

    public ArrayList<String> getUserVoted() {
        return userVoted;
    }

    public void setUserVoted(ArrayList<String> userVoted) {
        this.userVoted = userVoted;
    }

    public Date getContestEnd() {
        return contestEnd;
    }

    public void setContestEnd(Date contestEnd) {
        this.contestEnd = contestEnd;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
