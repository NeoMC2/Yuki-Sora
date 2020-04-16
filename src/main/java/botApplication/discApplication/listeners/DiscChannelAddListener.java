package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscRole;
import core.Engine;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;

public class DiscChannelAddListener extends ListenerAdapter {

    private Engine engine;

    private final DiscRole.RoleType[] permission1 = {DiscRole.RoleType.TempGamer, DiscRole.RoleType.Member, DiscRole.RoleType.Admin, DiscRole.RoleType.Mod};

    private final Permission[] voiceAndTextPermission = {Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ATTACH_FILES};
    private final Permission[] voicePermission = {Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};
    private final Permission[] textPermission = {Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ATTACH_FILES};

    public DiscChannelAddListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s != null) {
            if (!s.isSetupDone()) {
                return;
            }
        } else {
            return;
        }

        if (s.getCertificationChannelId().equals(event.getChannel().getId())) {
            return;
        }

        ArrayList<DiscRole.RoleType> roleTypes = engine.getDiscEngine().getSetupRoles().get(event.getGuild().getId());
        if (roleTypes == null) {
            addPermissionToRolesByType(s, event.getChannel(), textPermission, permission1, event.getGuild());
        } else {
            addPermissionToRolesByType(s, event.getChannel(), textPermission, roleTypes.toArray(), event.getGuild());
        }
        event.getChannel().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).complete();
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s != null) {
            if (!s.isSetupDone()) {
                return;
            }
        } else {
            return;
        }

        if (s.getCertificationChannelId().equals(event.getChannel().getId())) {
            return;
        }

        ArrayList<DiscRole.RoleType> roleTypes = engine.getDiscEngine().getSetupRoles().get(event.getGuild().getId());
        if (roleTypes == null) {
            addPermissionToRolesByType(s, event.getChannel(), voicePermission, permission1, event.getGuild());
        } else {
            addPermissionToRolesByType(s, event.getChannel(), voicePermission, roleTypes.toArray(), event.getGuild());
        }

        event.getChannel().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).complete();
    }

    @Override
    public void onCategoryCreate(CategoryCreateEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s != null) {
            if (!s.isSetupDone()) {
                return;
            }
        } else {
            return;
        }

        ArrayList<DiscRole.RoleType> roleTypes = engine.getDiscEngine().getSetupRoles().get(event.getGuild().getId());
        if (roleTypes == null) {
            addPermissionToRolesByType(s, event.getCategory(), voiceAndTextPermission, permission1, event.getGuild());
        } else {
            addPermissionToRolesByType(s, event.getCategory(), voiceAndTextPermission, roleTypes.toArray(), event.getGuild());
        }

        event.getCategory().createPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).complete();
    }

    private void addPermissionToRolesByType(DiscApplicationServer server, Channel channel, Permission[] p, Object[] roleType, Guild guild) {
        for (DiscRole role : server.getRoles()) {
            for (DiscRole.RoleType rtpy : role.getRoleTypes()) {
                for (Object rtpy2 : roleType) {
                    DiscRole.RoleType rtpyC = (DiscRole.RoleType) rtpy2;
                    if (rtpy == rtpyC) {
                        try {
                            channel.createPermissionOverride(guild.getRoleById(role.getId())).setAllow(p).complete();
                        } catch (Exception ignored){

                        }
                    }
                }
            }
        }
    }
}

