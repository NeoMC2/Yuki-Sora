package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.transaktion.Item;
import botApplication.discApplication.librarys.transaktion.monsters.Attack;
import botApplication.discApplication.librarys.transaktion.monsters.FightHandler;
import botApplication.discApplication.librarys.transaktion.monsters.Monster;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

import javax.management.MBeanRegistration;
import java.util.ArrayList;

public class DiscCmdPokemon implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "fight":
                    if (args.length > 1)
                        if (args[1].equals("accept")) {
                            for (FightHandler h : engine.getDiscEngine().getFightHandlers()) {
                                if (h.getTextChannel().getId().equals(event.getChannel().getId())) {
                                    h.setM2(event.getMember());
                                    h.begin();
                                    return;
                                }
                            }
                        }
                    for (FightHandler h : engine.getDiscEngine().getFightHandlers()) {
                        if (h.getTextChannel().getId().equals(event.getChannel().getId())) {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.pokemon.error.fightInProgress", user.getLang(), null), event.getChannel(), false);
                            return;
                        }
                    }
                    FightHandler h = new FightHandler(engine, event.getChannel(), event.getGuild());
                    engine.getDiscEngine().getFightHandlers().add(h);
                    h.setM1(event.getMember());
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.info.startedFight", user.getLang(), new String[]{event.getAuthor().getName()}), event.getChannel());
                    break;

                case "heal":
                    for (FightHandler ha : engine.getDiscEngine().getFightHandlers()) {
                        if (ha.getTextChannel().getId().equals(event.getChannel().getId())) {
                            return;
                        }
                    }
                    Monster m = user.getMonsters().get(Integer.parseInt(args[1]) + 1);
                    m.setHp(m.getMaxHp());
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.healed", user.getLang(), null), event.getChannel());
                    break;

                case "buy":
                    if (user.getCoins() >= 20) {
                        user.substractCoins(20);
                        Monster monster = engine.getDiscEngine().getTransaktionHandler().getRandomMonster(Item.Rarity.Normal);
                        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.buy", user.getLang(), new String[]{monster.getItemName(), Item.rarityToString(monster.getItemRarity())}), event.getChannel());
                        user.getMonsters().add(monster);
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.wallet.error.notEnoughMoney", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "info":
                case "pokemons":
                    if(args.length >1){

                    }
                    String msg = "";
                    for (int i = 0; i < user.getMonsters().size(); i++) {
                        msg += "[" + (i+1) + "]" + user.getMonsters().get(i).toString() + "\n\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendWarining(msg, event.getChannel());
                    break;

                case "attackinfo":
                    Monster mn = user.getMonsters().get(Integer.parseInt(args[1]));
                    if(mn == null){
                        engine.getDiscEngine().getTextUtils().sendError("Invalid", event.getChannel(), false);
                        return;
                    }
                    String msgg = "";
                    ArrayList<Attack> attacks = mn.getAllowedAttacks();
                    for (int i = 0; i < attacks.size(); i++) {
                        Attack c = attacks.get(i);
                        msgg += "[" + (i+1) + "] " + c.toString();
                    }
                    break;

                case "attackselect":
                case "selectattack":
                case "as":
                case "sa":
                    Monster mnn = user.getMonsters().get(Integer.parseInt(args[1]));
                    if(mnn == null){
                        engine.getDiscEngine().getTextUtils().sendError("Invalid", event.getChannel(), false);
                        return;
                    }
                    ArrayList<Attack> attackss = mnn.getAllowedAttacks();
                    Attack accs = attackss.get(Integer.parseInt(args[2]));
                    switch (args[3].toLowerCase()){
                        case "a1":
                            mnn.setA1(accs);
                            break;
                        case "a2":
                            mnn.setA2(accs);
                            break;
                        case "a3":
                            mnn.setA3(accs);
                            break;
                        case "a4":
                            mnn.setA4(accs);
                            break;
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
    public String help(Engine engine, DiscApplicationUser user) {
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
