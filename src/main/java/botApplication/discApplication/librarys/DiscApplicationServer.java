package botApplication.discApplication.librarys;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

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

    private boolean setupDone = false;
    private String certificationMessageId = "";
    private String certificationChannelId = "";
    private String defaultMemberRoleId = "";
    private String defaultTempGamerRoleId = "";
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
            if(r.getId().equals(role)){
                roles.remove(r);
            }
        }
        roles.add(role);
    }
}
