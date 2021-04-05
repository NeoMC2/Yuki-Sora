package botApplication.discApplication.librarys.autochan;

import botApplication.discApplication.utils.DiscUtilityBase;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class AutoChannel {
    private VoiceChannel vc;
    private AutoChannelType type;
    private Member createdBy;
    private boolean askedForChange = false;
    private boolean wasRenamedByMember = false;

    public void rename(String name){
        vc.getManager().setName(name + " [AC]").queue();
    }

    public AutoChannel recreate(VoiceChannel vc, AutoChannelType type) {
        this.vc = vc;
        this.type = type;
        if (vc.getMembers().size() != 0)
            this.createdBy = vc.getMembers().get(0);

        return this;
    }

    public AutoChannel createAutoChan(VoiceChannel vc, Guild gc, Member m) {
        this.vc = createNewAutoChannel(vc, gc, m, vc.getName());
        this.createdBy = m;
        this.type = AutoChannelType.Basic;
        return this;
    }

    public AutoChannel createGamingChan(VoiceChannel vc, Guild gc, Member m) {
        String name = DiscUtilityBase.getGame(m);
        if(name == null)
            name = "Gaming Lounge";
        this.vc = createNewAutoChannel(vc, gc, m, name);
        this.createdBy = m;
        this.type = AutoChannelType.Gaming;
        return this;
    }

    private VoiceChannel createNewAutoChannel(VoiceChannel vc, Guild gc, Member m, String name) {
        if(m == null || vc == null)
            return null;
        else if(m.getVoiceState() == null)
            return null;
        else if(m.getVoiceState().getChannel() == null)
            return null;
        else if(!m.getVoiceState().getChannel().getId().equals(vc.getId()))
            return null;

        VoiceChannel nvc = gc.createVoiceChannel(name + " [AC]")
                .setBitrate(vc.getBitrate())
                .setUserlimit(vc.getUserLimit())
                .complete();

        if (vc.getParent() != null)
            nvc.getManager().setParent(vc.getParent()).queue();

        gc.modifyVoiceChannelPositions().selectPosition(nvc).moveTo(vc.getPosition() + 1).queue();
        for (PermissionOverride or : vc.getPermissionOverrides()) {
            nvc.createPermissionOverride(or.getRole()).setAllow(or.getAllowed()).setDeny(or.getDenied()).complete();
        }
        try {
            gc.moveVoiceMember(m, nvc).complete();
        } catch (Exception e){
            nvc.delete().queue();
        }
        return nvc;
    }

    public VoiceChannel getVc() {
        return vc;
    }

    public void setVc(VoiceChannel vc) {
        this.vc = vc;
    }

    public AutoChannelType getType() {
        return type;
    }

    public void setType(AutoChannelType type) {
        this.type = type;
    }

    public Member getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Member createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isAskedForChange() {
        return askedForChange;
    }

    public void setAskedForChange(boolean askedForChange) {
        this.askedForChange = askedForChange;
    }

    public boolean isWasRenamedByMember() {
        return wasRenamedByMember;
    }

    public void setWasRenamedByMember(boolean wasRenamedByMember) {
        this.wasRenamedByMember = wasRenamedByMember;
    }

    public enum AutoChannelType {
        Basic, Gaming
    }
}
