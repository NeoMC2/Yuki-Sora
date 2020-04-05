package botApplication.discApplication.utils;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import core.Engine;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class DiscUtilityBase {

    Engine engine;

    public DiscUtilityBase(Engine engine) {
        this.engine = engine;
    }

    public boolean userHasGuildAdminPermission(Member member, Guild guild, TextChannel textChannel) {
        boolean hasPermission = false;
        for (int i = 0; member.getRoles().size() > i; i++) {
            for (int a = 0; member.getRoles().get(i).getPermissions().size() > a; i++) {
                if (member.getRoles().get(i).getPermissions().get(a).ADMINISTRATOR != null) {
                    hasPermission = true;
                    break;
                }
            }
        }
        if (hasPermission) {
            return true;
        } else {
            engine.getDiscEngine().getTextUtils().sendError("Du hast keine Berechtigung um diesen Command auzuführen! Dafür musst du Admin auf diesem Guild sein!", textChannel, engine.getProperties().middleTime, true);
            return false;
        }
    }

    public DiscApplicationUser lookForUserById(User discuser){
        DiscApplicationUser user = null;

        try {
            user = engine.getDiscEngine().getFilesHandler().getUserById(discuser.getId());
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("![Ai Engine] " + discuser.getId() + " User not found!",true);
        }

        if (user == null) {
            try {
                user = engine.getDiscEngine().getFilesHandler().createNewUser(discuser, DiscCertificationLevel.Temp);
            } catch (Exception e) {
                System.out.println("Fatal error in ServerMessageListener.sendOwnCommand()---user cant load!!!");
            }
        }
        return user;
    }

    public DiscApplicationServer lookForServer(Guild guild){
        DiscApplicationServer server = null;

        try {
            server = engine.getDiscEngine().getFilesHandler().getServerById(guild.getId());
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("![Ai Engine] " + guild.getId() + " Server not found!", true);
        }

        if (server == null) {
            try {
                server = engine.getDiscEngine().getFilesHandler().createNewServer(guild);
            } catch (Exception e) {
                System.out.println("Fatal error in ServerMessageListener.sendOwnCommand()---server cant load!!!");
            }
        }
        return server;
    }
}
