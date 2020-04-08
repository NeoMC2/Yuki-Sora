package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import core.Engine;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscChannelAddListener extends ListenerAdapter {

    private Engine engine;

    public DiscChannelAddListener(Engine engine) {
        this.engine = engine;
    }

    public void onTextChannelCreate(TextChannelCreateEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s != null) {
            if (!s.isSetupDone()) {
                return;
            }
        } else {
            return;
        }

        if(s.getCertificationChannelId().equals(event.getChannel().getId())){
            return;
        }

        event.getChannel().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.ALL_PERMISSIONS).complete();
        event.getChannel().createPermissionOverride(event.getGuild().getRoleById(s.getDefaultMemberRoleId())).setAllow(Permission.ALL_TEXT_PERMISSIONS).complete();
        event.getChannel().createPermissionOverride(event.getGuild().getRoleById(s.getDefaultTempGamerRoleId())).setAllow(Permission.ALL_TEXT_PERMISSIONS).complete();
    }

    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s != null) {
            if (!s.isSetupDone()) {
                return;
            }
        } else {
            return;
        }

        if(s.getCertificationChannelId().equals(event.getChannel().getId())){
            return;
        }

        event.getChannel().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.ALL_PERMISSIONS).complete();
        event.getChannel().createPermissionOverride(event.getGuild().getRoleById(s.getDefaultMemberRoleId())).setAllow(Permission.ALL_VOICE_PERMISSIONS).complete();
        event.getChannel().createPermissionOverride(event.getGuild().getRoleById(s.getDefaultTempGamerRoleId())).setAllow(Permission.ALL_VOICE_PERMISSIONS).complete();
    }
}
