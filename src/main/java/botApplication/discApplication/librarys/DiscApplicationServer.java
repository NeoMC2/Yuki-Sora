package botApplication.discApplication.librarys;

import net.dv8tion.jda.core.entities.Guild;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscApplicationServer implements Serializable {

    private static final long serialVersionUID = 42L;

    private String serverName;
    private String serverID;
    private String serverYTPlaylist;
    private boolean listenerEnabled = false;
    private boolean musicListenerEnabled = false;
    private String musicListenerName = "djNexus";

    private ArrayList<String> defaultRoles = new ArrayList<>();

    private String workChannelId;
    private String shopChannelId;

    private boolean setupMode = false;
    private boolean setupDone = false;
    private String certificationMessageId = "";
    private String certificationChannelId = "";
    private String defaultMemberRoleId = "";
    private String defaultTempGamerRoleId = "";
    private String welcomeMessageChannel = "";
    private String welcomeText = "Bist du nur hier um kurz ein paar Runden zu zocken? Dann drück einfach \\uD83C\\uDFAE\\n\\nWenn du dem Server beitreten willst, drück einfach ✅\\n\\nAnsonsten ❌, das ändert aber nix loolz";
    private ArrayList<DiscRole> roles = new ArrayList<>();

    private ArrayList<String> autoChannels = new ArrayList<>();

    public DiscApplicationServer(Guild guild) {
        this.serverName = guild.getName();
        this.serverID = guild.getId();
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerYTPlaylist() {
        return serverYTPlaylist;
    }

    public void setServerYTPlaylist(String serverYTPlaylist) {
        this.serverYTPlaylist = serverYTPlaylist;
    }

    public boolean isListenerEnabled() {
        return listenerEnabled;
    }

    public void setListenerEnabled(boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }

    public boolean isMusicListenerEnabled() {
        return musicListenerEnabled;
    }

    public void setMusicListenerEnabled(boolean musicListenerEnabled) {
        this.musicListenerEnabled = musicListenerEnabled;
    }

    public String getMusicListenerName() {
        return musicListenerName;
    }

    public void setMusicListenerName(String musicListenerName) {
        this.musicListenerName = musicListenerName;
    }

    public boolean isSetupDone() {
        return setupDone;
    }

    public void setSetupDone(boolean setupDone) {
        this.setupDone = setupDone;
    }

    public String getCertificationChannelId() {
        return certificationChannelId;
    }

    public void setCertificationChannelId(String certificationChannelId) {
        this.certificationChannelId = certificationChannelId;
    }

    public String getCertificationMessageId() {
        return certificationMessageId;
    }

    public void setCertificationMessageId(String certificationMessageId) {
        this.certificationMessageId = certificationMessageId;
    }

    public ArrayList<String> getAutoChannels() {
        return autoChannels;
    }

    public void setAutoChannels(ArrayList<String> autoChannels) {
        this.autoChannels = autoChannels;
    }

    public String getDefaultMemberRoleId() {
        return defaultMemberRoleId;
    }

    public void setDefaultMemberRoleId(String defaultMemberRoleId) {
        this.defaultMemberRoleId = defaultMemberRoleId;
    }

    public String getDefaultTempGamerRoleId() {
        return defaultTempGamerRoleId;
    }

    public void setDefaultTempGamerRoleId(String defaultTempGamerRoleId) {
        this.defaultTempGamerRoleId = defaultTempGamerRoleId;
    }

    public ArrayList<DiscRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<DiscRole> roles) {
        this.roles = roles;
    }

    public void addRole(DiscRole role){
        for (DiscRole r:roles) {
            if(r.getId().equals(role.getId())){
                roles.remove(r);
            }
        }
        roles.add(role);
    }

    public boolean isSetupMode() {
        return setupMode;
    }

    public void setSetupMode(boolean setupMode) {
        this.setupMode = setupMode;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public ArrayList<String> getDefaultRoles() {
        if(defaultRoles == null) {
            defaultRoles = new ArrayList<>();
        }
        return defaultRoles;
    }

    public void setDefaultRoles(ArrayList<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    public String getWorkChannelId() {
        return workChannelId;
    }

    public void setWorkChannelId(String workChannelId) {
        this.workChannelId = workChannelId;
    }

    public String getShopChannelId() {
        return shopChannelId;
    }

    public void setShopChannelId(String shopChannelId) {
        this.shopChannelId = shopChannelId;
    }

    public String getWelcomeMessageChannel() {
        return welcomeMessageChannel;
    }

    public void setWelcomeMessageChannel(String welcomeMessageChannel) {
        this.welcomeMessageChannel = welcomeMessageChannel;
    }
}
