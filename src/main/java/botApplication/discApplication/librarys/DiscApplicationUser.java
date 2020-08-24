package botApplication.discApplication.librarys;

import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.Monster;
import botApplication.discApplication.librarys.job.UserJob;
import net.dv8tion.jda.api.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DiscApplicationUser implements Serializable {

    private static final long serialVersionUID = 42L;

    private String userName;
    private String ytPlaylist;
    private String userId;
    private boolean admin = false;
    private long telegramId;
    private DiscCertificationLevel discCertificationLevel;
    private ArrayList<String> servers = new ArrayList<>();
    private String lang = "en";

    private Date lastWorkTime;
    private long coins = 20;
    private UserJob userJob;
    private int xp;
    private int level;

    private int maxMonsters = 10;
    private int maxItems = 30;

    private ArrayList<Monster> monsters = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();

    private boolean saidHello = false;

    public DiscApplicationUser(User user, DiscCertificationLevel discCertificationLevel) {
        this.userName = user.getName();
        this.userId = user.getId();
        this.discCertificationLevel = discCertificationLevel;
    }

    public boolean isMonsterInvFull() {
        return monsters.size() >= maxMonsters;
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

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void substractCoins(int coins) {
        this.coins -= coins;
    }

    public boolean isSaidHello() {
        return saidHello;
    }

    public void setSaidHello(boolean saidHello) {
        this.saidHello = saidHello;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(ArrayList<Monster> monsters) {
        this.monsters = monsters;
    }

    public void addMonster(Monster m) throws Exception {
        if (monsters.size() >= maxMonsters) {
            throw new Exception("To many monsters");
        }
        monsters.add(m);
    }

    public int getMaxMonsters() {
        return maxMonsters;
    }

    public void setMaxMonsters(int maxMonsters) {
        this.maxMonsters = maxMonsters;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) throws Exception {
        if (items.size() >= maxItems) {
            throw new Exception("To many Items");
        }
        items.add(item);
    }

    public void upgrade() {

    }
}