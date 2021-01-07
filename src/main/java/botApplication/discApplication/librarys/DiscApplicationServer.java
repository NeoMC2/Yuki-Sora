package botApplication.discApplication.librarys;

import botApplication.discApplication.librarys.dungeon.queue.DungeonChannelHandler;
import botApplication.discApplication.librarys.dungeon.queue.DungeonQueueHandler;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscApplicationServer implements Serializable {

    private static final long serialVersionUID = 42L;

    private boolean edit = false;

    private String serverName;
    private String serverID;
    private String serverYTPlaylist;
    private boolean listenerEnabled = false;
    private boolean musicListenerEnabled = false;
    private String musicListenerName = "djNexus";

    private ArrayList<String> defaultRoles = new ArrayList<>();

    private String workChannelId;
    private String shopChannelId;

    private String vipRoleId;
    private String primeRoleId;
    private String boosterRoleId;
    private String boosterCategoryId;

    private boolean setupMode = false;
    private boolean setupDone = false;
    private String certificationMessageId = "";
    private String certificationChannelId = "";
    private String defaultMemberRoleId = "";
    private String defaultTempGamerRoleId = "";
    private String welcomeMessageChannel = "";
    private String welcomeText = "";
    private ArrayList<DiscRole> roles = new ArrayList<>();

    private String memberCountCategoryId = "";

    private ArrayList<String> autoChannels = new ArrayList<>();
    private DungeonQueueHandler dungeonQueueHandler;

    private String baitChannel;

    private boolean moveMemberOnSDeafen = true;

    public DiscApplicationServer(Guild guild) {
        edit = true;
        this.serverName = guild.getName();
        this.serverID = guild.getId();
    }

    public void generateFromJSON(JSONObject obj){
        edit = false;
        serverName = (String) obj.get("serverName");
        serverID = (String) obj.get("serverId");
        serverYTPlaylist = (String) obj.get("serverYtPlaylist");
        certificationMessageId = (String) obj.get("certificationMessageId");
        certificationChannelId = (String) obj.get("certificationChannelId");
        welcomeMessageChannel = (String) obj.get("welcomeMessageChannelId");
        welcomeText = (String) obj.get("welcomeText");
        memberCountCategoryId = (String) obj.get("memberCountStatsChannelId");
        defaultMemberRoleId  = (String) obj.get("defaultMemberRoleId");
        defaultTempGamerRoleId = (String) obj.get("defaultTempGamerRoleId");
        primeRoleId = (String) obj.get("primeRoleId");
        vipRoleId = (String) obj.get("vipRoleId");
        defaultRoles = jsonArrayToArray((JSONArray) obj.get("roleIds"));
        autoChannels = jsonArrayToArray((JSONArray) obj.get("autoChannelIds"));

        try {
            boosterRoleId = (String) obj.get("boosterRoleId");
            boosterCategoryId = (String) obj.get("boosterCategoryId");
        } catch (Exception ignored){
        }

        String dungeonMessage = (String) obj.get("dungeonQueueMessage");
        String dungeonEmoji = (String) obj.get("dungeonEmoji");
        JSONArray dungeonChans = (JSONArray) obj.get("dungeonChan");
        JSONArray dungeonRoles = (JSONArray) obj.get("dungeonChanRoles");

        if(dungeonEmoji != null && dungeonMessage != null && dungeonChans != null && dungeonRoles !=null){
            dungeonQueueHandler = new DungeonQueueHandler();
            dungeonQueueHandler.setMsgId(dungeonMessage);
            dungeonQueueHandler.setEmoji(dungeonEmoji);

            for (int i = 0; i < dungeonChans.size(); i++) {
                String chanId = (String) dungeonChans.get(i);
                String roleId = (String) dungeonRoles.get(i);
                DungeonChannelHandler c = new DungeonChannelHandler(chanId, roleId);
                dungeonQueueHandler.getChannels().add(c);
            }
        }

    }

    public void update(JSONObject obj){

    }

    private ArrayList<String> jsonArrayToArray(JSONArray a){
        ArrayList<String> s = new ArrayList<>();
        for (Object o:a) {
            String st = (String) o;
            s.add(st);
        }
        return s;
    }

    public void updateServerStats(Engine engine) {
        Guild g = engine.getDiscEngine().getBotJDA().getGuildById(serverID);
        try {
            g.getCategoryById(memberCountCategoryId).getManager().setName("\uD83D\uDCCAMember Count: " + g.getMembers().size()).queue();
        } catch (Exception e) {

        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        edit = true;
        this.serverName = serverName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        edit = true;
        this.serverID = serverID;
    }

    public String getServerYTPlaylist() {
        return serverYTPlaylist;
    }

    public void setServerYTPlaylist(String serverYTPlaylist) {
        edit = true;
        this.serverYTPlaylist = serverYTPlaylist;
    }

    public boolean isListenerEnabled() {
        return listenerEnabled;
    }

    public void setListenerEnabled(boolean listenerEnabled) {
        edit = true;
        this.listenerEnabled = listenerEnabled;
    }

    public boolean isMusicListenerEnabled() {
        return musicListenerEnabled;
    }

    public void setMusicListenerEnabled(boolean musicListenerEnabled) {
        edit = true;
        this.musicListenerEnabled = musicListenerEnabled;
    }

    public String getMusicListenerName() {
        return musicListenerName;
    }

    public void setMusicListenerName(String musicListenerName) {
        edit = true;
        this.musicListenerName = musicListenerName;
    }

    public boolean isSetupDone() {
        return setupDone;
    }

    public void setSetupDone(boolean setupDone) {
        edit = true;
        this.setupDone = setupDone;
    }

    public String getCertificationChannelId() {
        return certificationChannelId;
    }

    public void setCertificationChannelId(String certificationChannelId) {
        edit = true;
        this.certificationChannelId = certificationChannelId;
    }

    public String getCertificationMessageId() {
        return certificationMessageId;
    }

    public void setCertificationMessageId(String certificationMessageId) {
        edit = true;
        this.certificationMessageId = certificationMessageId;
    }

    public ArrayList<String> getAutoChannels(){
        return autoChannels;
    }

    public void addAutoChannel(String id){
        edit = true;
        autoChannels.add(id);
    }

    public void removeAutoChannel(String id){
        edit = true;
        autoChannels.remove(id);
    }

    public void setAutoChannels(ArrayList<String> autoChannels) {
        edit = true;
        this.autoChannels = autoChannels;
    }

    public String getDefaultMemberRoleId() {
        return defaultMemberRoleId;
    }

    public void setDefaultMemberRoleId(String defaultMemberRoleId) {
        edit = true;
        this.defaultMemberRoleId = defaultMemberRoleId;
    }

    public String getDefaultTempGamerRoleId() {
        return defaultTempGamerRoleId;
    }

    public void setDefaultTempGamerRoleId(String defaultTempGamerRoleId) {
        edit = true;
        this.defaultTempGamerRoleId = defaultTempGamerRoleId;
    }

    public ArrayList<DiscRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<DiscRole> roles) {
        edit = true;
        this.roles = roles;
    }

    public void addRole(DiscRole role) {
        edit = true;
        for (DiscRole r : roles) {
            if (r.getId().equals(role.getId())) {
                roles.remove(r);
            }
        }
        roles.add(role);
    }

    public boolean isSetupMode() {
        return setupMode;
    }

    public void setSetupMode(boolean setupMode) {
        edit = true;
        this.setupMode = setupMode;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        edit = true;
        this.welcomeText = welcomeText;
    }

    public ArrayList<String> getDefaultRoles() {
        if (defaultRoles == null) {
            defaultRoles = new ArrayList<>();
        }
        return defaultRoles;
    }

    public void setDefaultRoles(ArrayList<String> defaultRoles) {
        edit = true;
        this.defaultRoles = defaultRoles;
    }

    public String getWorkChannelId() {
        return workChannelId;
    }

    public void setWorkChannelId(String workChannelId) {
        edit = true;
        this.workChannelId = workChannelId;
    }

    public String getShopChannelId() {
        return shopChannelId;
    }

    public void setShopChannelId(String shopChannelId) {
        edit = true;
        this.shopChannelId = shopChannelId;
    }

    public String getWelcomeMessageChannel() {
        return welcomeMessageChannel;
    }

    public void setWelcomeMessageChannel(String welcomeMessageChannel) {
        edit = true;
        this.welcomeMessageChannel = welcomeMessageChannel;
    }

    public String getMemberCountCategoryId() {
        return memberCountCategoryId;
    }

    public void setMemberCountCategoryId(String memberCountCategoryId) {
        edit = true;
        this.memberCountCategoryId = memberCountCategoryId;
    }

    public DungeonQueueHandler getDungeonQueueHandler() {
        return dungeonQueueHandler;
    }

    public void setDungeonQueueHandler(DungeonQueueHandler dungeonQueueHandler) {
        edit = true;
        this.dungeonQueueHandler = dungeonQueueHandler;
    }

    public String getBaitChannel() {
        return baitChannel;
    }

    public void setBaitChannel(String baitChannel) {
        this.baitChannel = baitChannel;
    }

    public boolean isMoveMemberOnSDeafen() {
        return moveMemberOnSDeafen;
    }

    public void setMoveMemberOnSDeafen(boolean moveMemberOnSDeafen) {
        this.moveMemberOnSDeafen = moveMemberOnSDeafen;
    }

    public String getVipRoleId() {
        return vipRoleId;
    }

    public void setVipRoleId(String vipRoleId) {
        this.vipRoleId = vipRoleId;
    }

    public String getPrimeRoleId() {
        return primeRoleId;
    }

    public void setPrimeRoleId(String primeRoleId) {
        this.primeRoleId = primeRoleId;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String getBoosterRoleId() {
        return boosterRoleId;
    }

    public void setBoosterRoleId(String boosterRoleId) {
        edit = true;
        this.boosterRoleId = boosterRoleId;
    }

    public String getBoosterCategoryId() {
        return boosterCategoryId;
    }

    public void setBoosterCategoryId(String boosterCategoryId) {
        edit = true;
        this.boosterCategoryId = boosterCategoryId;
    }
}
