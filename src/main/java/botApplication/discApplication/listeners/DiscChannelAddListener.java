package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscRole;
import botApplication.discApplication.librarys.autochan.AutoChannel;
import core.Engine;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class DiscChannelAddListener extends ListenerAdapter {

    private final DiscRole.RoleType[] permission1 = {DiscRole.RoleType.TempGamer, DiscRole.RoleType.Member, DiscRole.RoleType.Admin, DiscRole.RoleType.Mod};
    private final Permission[] voiceAndTextPermission = {Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ATTACH_FILES, Permission.VIEW_CHANNEL};
    private final Permission[] voicePermission = {Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VIEW_CHANNEL};
    private final Permission[] textPermission = {Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY, Permission.MESSAGE_READ, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ATTACH_FILES, Permission.VIEW_CHANNEL};
    private final Engine engine;

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
            if (!s.isSetupMode()) {
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
            if (!s.isSetupMode()) {
                return;
            }
            for (AutoChannel ac : DiscVoiceListener.activeAutoChannels) {
                VoiceChannel vc = ac.getVc();
                if (vc.getId().equals(event.getChannel().getId())) {
                    return;
                }
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
            if (!s.isSetupMode()) {
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

    private void addPermissionToRolesByType(DiscApplicationServer server, Category channel, Permission[] p, Object[] roleType, Guild guild) {
        for (DiscRole role : server.getRoles()) {
            for (DiscRole.RoleType rtpy : role.getRoleTypes()) {
                for (Object rtpy2 : roleType) {
                    DiscRole.RoleType rtpyC = (DiscRole.RoleType) rtpy2;
                    if (rtpy == rtpyC) {
                        try {
                            channel.createPermissionOverride(guild.getRoleById(role.getId())).setAllow(p).complete();
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
    }

    private void addPermissionToRolesByType(DiscApplicationServer server, VoiceChannel channel, Permission[] p, Object[] roleType, Guild guild) {
        for (DiscRole role : server.getRoles()) {
            for (DiscRole.RoleType rtpy : role.getRoleTypes()) {
                for (Object rtpy2 : roleType) {
                    DiscRole.RoleType rtpyC = (DiscRole.RoleType) rtpy2;
                    if (rtpy == rtpyC) {
                        try {
                            channel.createPermissionOverride(guild.getRoleById(role.getId())).setAllow(p).complete();
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
    }

    private void addPermissionToRolesByType(DiscApplicationServer server, TextChannel channel, Permission[] p, Object[] roleType, Guild guild) {
        for (DiscRole role : server.getRoles()) {
            for (DiscRole.RoleType rtpy : role.getRoleTypes()) {
                for (Object rtpy2 : roleType) {
                    DiscRole.RoleType rtpyC = (DiscRole.RoleType) rtpy2;
                    if (rtpy == rtpyC) {
                        try {
                            channel.createPermissionOverride(guild.getRoleById(role.getId())).setAllow(p).complete();
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
    }
}

