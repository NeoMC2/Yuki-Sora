package botApplication.discApplication.librarys;

import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import botApplication.discApplication.librarys.transaktion.job.UserJob;
import net.dv8tion.jda.core.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DiscApplicationUser implements Serializable {

    public static final long serialVersionUID = 42L;

    private String userName;
    private String ytPlaylist;
    private String userId;
    private boolean admin = false;
    private long telegramId;
    private DiscCertificationLevel discCertificationLevel;
    private ArrayList<String> servers = new ArrayList<>();
    private String lang = "en";

    private Date lastWorkTime;
    private long coins;
    private UserJob userJob;
    private int xp;
    private int level;

    private boolean saidHello = false;

    public DiscApplicationUser(User user, DiscCertificationLevel discCertificationLevel) {
        this.userName = user.getName();
        this.userId = user.getId();
        this.discCertificationLevel = discCertificationLevel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getYtPlaylist() {
        return ytPlaylist;
    }

    public void setYtPlaylist(String ytPlaylist) {
        this.ytPlaylist = ytPlaylist;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public DiscCertificationLevel getDiscCertificationLevel() {
        return discCertificationLevel;
    }

    public void setDiscCertificationLevel(DiscCertificationLevel discCertificationLevel) {
        this.discCertificationLevel = discCertificationLevel;
    }

    public ArrayList<String> getServers() {
        return servers;
    }

    public void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Date getLastWorkTime() {
        return lastWorkTime;
    }

    public void setLastWorkTime(Date lastWorkTime) {
        this.lastWorkTime = lastWorkTime;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public UserJob getUserJob() {
        return userJob;
    }

    public void setUserJob(UserJob userJob) {
        this.userJob = userJob;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addCoins(int coins){
        this.coins+=coins;
    }

    public void substractCoins(int coins){
        this.coins-=coins;
    }

    public boolean isSaidHello() {
        return saidHello;
    }

    public void setSaidHello(boolean saidHello) {
        this.saidHello = saidHello;
    }
}