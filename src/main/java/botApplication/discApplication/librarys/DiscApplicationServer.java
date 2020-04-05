package botApplication.discApplication.librarys;

import net.dv8tion.jda.core.entities.Guild;

import java.io.Serializable;

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
    private String memberRoleId = "";
    private String tempGamerRoleId = "";

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

    public String getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(String memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public String getTempGamerRoleId() {
        return tempGamerRoleId;
    }

    public void setTempGamerRoleId(String tempGamerRoleId) {
        this.tempGamerRoleId = tempGamerRoleId;
    }
}
