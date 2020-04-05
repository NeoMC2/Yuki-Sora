package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class DiscCmdSetup implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return engine.getDiscEngine().getUtilityBase().userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel());
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if(args.length>=1){
            switch (args[0].toLowerCase()){
                case "channel":
                    if(args.length>=2){
                        server.setCertificationChannelId(args[1]);
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError("Zu wenig Argumente", event.getChannel(), false);
                    }
                    break;

                case "role":
                    if(args.length>=3){
                        switch (args[1].toLowerCase()){
                            case "gamer":
                                server.setTempGamerRoleId(args[2]);
                                break;

                            case "member":
                                server.setMemberRoleId(args[2]);
                                break;
                        }

                    } else {
                        engine.getDiscEngine().getTextUtils().sendError("Zu wenig Argumente", event.getChannel(), false);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine) {
        return "channel <channelid>\nrole gamer/member <role id>";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void setupServer(GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine){

    }
}
