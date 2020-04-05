package botApplication.discApplication.librarys.certification;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class DiscCertificationHandler {

    private final String consMsgDef = "[Certification Handler]";
    private Engine engine;

    public DiscCertificationHandler(Engine engine) {
        this.engine = engine;
    }

    public void addMemberCertification(Member member, Guild guild) {
        if (!exec(guild))
            return;
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().createNewUser(member.getUser(), DiscCertificationLevel.Member);
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        usr.getServers().add(guild.getId());
        try {
            member.getRoles().add(guild.getRoleById(server.getMemberRoleId()));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Role is invalid!!!", true);
        }
    }

    public void removeCertification(Member member, Guild guild) {
        if (!exec(guild))
            return;
        DiscApplicationUser usr = null;
        try {
            usr = engine.getDiscEngine().getFilesHandler().getUserById(member.getUser().getId());
        } catch (Exception e) {
            //Just doesn`t exist...no problem
        }
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        if(usr != null){
            usr.getServers().remove(guild.getId());

            if(usr.getDiscCertificationLevel() == DiscCertificationLevel.Member){
                try {
                    member.getRoles().remove(guild.getRoleById(server.getMemberRoleId()));
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Role is invalid!!!", true);
                }
            }
            if(usr.getDiscCertificationLevel() == DiscCertificationLevel.TempGamer){
                try {
                    member.getRoles().remove(guild.getRoleById(server.getTempGamerRoleId()));
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!Role is invalid!!!", true);
                }
            }
        }
    }

    public void addTempGameCertification(Member member, Guild guild) {
        if (!exec(guild))
            return;
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().createNewUser(member.getUser(), DiscCertificationLevel.TempGamer);
        DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());

        usr.getServers().add(guild.getId());
        try {
            member.getRoles().add(guild.getRoleById(server.getTempGamerRoleId()));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Role is invalid!!!", true);
        }
    }

    private boolean exec(Guild guild) {
        if (engine.getDiscEngine().getFilesHandler().getServers().containsKey(guild.getId())) {
            DiscApplicationServer server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());
            if(server.getCertificationChannelId().equals("")||server.getMemberRoleId().equals("")||server.getTempGamerRoleId().equals("")){
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
