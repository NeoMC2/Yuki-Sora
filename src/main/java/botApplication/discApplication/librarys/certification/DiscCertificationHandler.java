package botApplication.discApplication.librarys.certification;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.ArrayList;

public class DiscCertificationHandler {

    private final String consMsgDef = "[Certification Handler]";
    private final Engine engine;

    public DiscCertificationHandler(Engine engine) {
        this.engine = engine;
    }

    public void addMemberCertification(Member member, Guild guild) {
        ArrayList<Role> roles = new ArrayList<>();
        engine.getUtilityBase().printOutput(consMsgDef + " !Add member certification: " + member.getUser().getName() + " Id: " + member.getUser().getId() + "!", true);
        DiscApplicationUser usr = DiscUtilityBase.lookForUserById(member.getUser(), engine);
        boolean neww = false;
        if(usr == null) {
            usr = engine.getDiscEngine().getFilesHandler().createNewUser(member.getUser(), DiscCertificationLevel.Member);
            neww = true;
        }
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        usr.getServers().add(guild.getId());
        roles.add(guild.getRoleById(server.getDefaultMemberRoleId()));
        for (String s : server.getDefaultRoles()) {
            roles.add(guild.getRoleById(s));
        }
        for (Role r : roles) {
            try {
                guild.addRoleToMember(member, r).queue();
            } catch (Exception e) {

            }
        }

        try {
            if (neww) {
                engine.getDiscEngine().getTextUtils().sendCustomMessage("We have a new Member here 🎊. Say hello to " + member.getUser().getName() + "!", guild.getTextChannelById(server.getWelcomeMessageChannel()), "Welcome", Color.YELLOW);
                usr.setSaidHello(true);
            }
        } catch (Exception e) {

        }
    }

    public void removeCertification(Member member, Guild guild) {
        ArrayList<Role> roles = new ArrayList<>();
        engine.getUtilityBase().printOutput(consMsgDef + " !Remove member certification: " + member.getUser().getName() + " Id: " + member.getUser().getId() + "!", true);
        DiscApplicationUser usr = null;
        try {
            usr = engine.getDiscEngine().getFilesHandler().getUserById(member.getUser().getId());
        } catch (Exception e) {
            //Just doesn`t exist...no problem
        }
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        if (usr != null) {
            usr.getServers().remove(server);
        }

        roles.add(guild.getRoleById(server.getDefaultMemberRoleId()));
        roles.add(guild.getRoleById(server.getDefaultTempGamerRoleId()));

        for (String s : server.getDefaultRoles()) {
            roles.add(guild.getRoleById(s));
        }

        for (Role r : roles) {
            guild.removeRoleFromMember(member, r).queue();
        }
    }

    public void addTempGameCertification(Member member, Guild guild) {
        engine.getUtilityBase().printOutput(consMsgDef + " !Add Temp Gamer certification: " + member.getUser().getName() + " Id: " + member.getUser().getId() + "!", true);
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().createNewUser(member.getUser(), DiscCertificationLevel.TempGamer);
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        usr.getServers().add(guild.getId());
        try {
            guild.addRoleToMember(member, guild.getRoleById(server.getDefaultTempGamerRoleId())).queue();
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Role is invalid!!!", true);
        }
    }
}
