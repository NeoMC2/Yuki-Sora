package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.transaktion.monsters.FightHandler;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class DiscCmdPokemon implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if(args.length>=1){
            switch (args[0]){
                case "fight":
                    if(args[1].equals("accept")){
                        for (FightHandler h:engine.getDiscEngine().getFightHandlers()) {
                            if(h.getTextChannel().getId().equals(event.getChannel().getId())){
                                h.setM2(event.getMember());
                                h.begin();
                                return;
                            }
                        }
                    }
                    for (FightHandler h:engine.getDiscEngine().getFightHandlers()) {
                        if(h.getTextChannel().getId().equals(event.getChannel().getId())){
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.pokemon.error.fightInProgress", user.getLang(), null), event.getChannel(), false);
                            return;
                        }
                    }
                    FightHandler h = new FightHandler(engine, event.getChannel(), event.getGuild());
                    h.setM1(event.getMember());
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
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
