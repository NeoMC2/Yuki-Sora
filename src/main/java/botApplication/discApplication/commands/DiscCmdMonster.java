package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.Attack;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.librarys.item.monsters.Monster;
import core.Engine;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;

public class DiscCmdMonster implements DiscCommand {
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
                                    if (h.getM2() != null || h.getM1().getUser().getId().equals(event.getAuthor().getId())) {
                                        return;
                                    }
                                    h.setM2(event.getMember());
                                    h.begin();
                                    return;
                                }
                            }
                        }
                    for (FightHandler h : engine.getDiscEngine().getFightHandlers()) {
                        if (h.getTextChannel().getId().equals(event.getChannel().getId())) {
                            if (h.getM2() != null)
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.pokemon.error.fightInProgress", user.getLang(), null), event.getChannel(), false);
                            else
                                engine.getDiscEngine().getFightHandlers().remove(h);
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
                    Monster m = user.getMonsters().get(Integer.parseInt(args[1]) - 1);
                    m.setHp(m.getMaxHp());
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.healed", user.getLang(), null), event.getChannel());
                    break;

                case "buy":
                    if (user.isMonsterInvFull()) {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.pokemon.error.toManyPokemons", user.getLang(), null), event.getChannel(), false);
                        return;
                    }
                    if (user.getCoins() >= 20) {
                        user.substractCoins(20);
                        Monster monster = engine.getDiscEngine().getTransaktionHandler().getRandomMonster(Item.Rarity.Normal);
                        EmbedBuilder mb = new EmbedBuilder()
                                .setDescription(engine.lang("cmd.pokemon.success.buy", user.getLang(), new String[]{monster.getItemName(), Item.rarityToString(monster.getItemRarity())}))
                                .setAuthor("Got " + monster.getItemName(), monster.getImgUrl())
                                .setColor(Color.CYAN)
                                .setThumbnail(monster.getImgUrl());
                        event.getChannel().sendMessage(mb.build()).queue();
                        user.getMonsters().add(monster);
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.wallet.error.notEnoughMoney", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "list":
                case "info":
                case "pokemons":
                    String msg = "Pokemons\n\n";
                    if (args.length > 1) {
                        int i = Integer.parseInt(args[1]);
                        msg = "[" + i + "]\n" + user.getMonsters().get(i - 1).toString();
                    } else {
                        for (int i = 0; i < user.getMonsters().size(); i++) {
                            msg += "[" + (i + 1) + "] " + user.getMonsters().get(i).getItemName() + "\n";
                        }
                    }
                    engine.getDiscEngine().getTextUtils().sendWarining(msg, event.getChannel());
                    break;

                case "ai":
                case "attackinfo":
                    Monster mn = user.getMonsters().get(Integer.parseInt(args[1]) - 1);
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid", event.getChannel(), false);
                        return;
                    }
                    String msgg = "";
                    ArrayList<Attack> attacks = mn.getAllowedAttacks();
                    for (int i = 0; i < attacks.size(); i++) {
                        Attack c = attacks.get(i);
                        msgg += "[" + (i + 1) + "] " + c.toString();
                    }
                    engine.getDiscEngine().getTextUtils().sendWarining(msgg, event.getChannel());
                    break;

                case "attackselect":
                case "selectattack":
                case "as":
                case "sa":
                    Monster mnn = user.getMonsters().get(Integer.parseInt(args[1]) + 1);
                    if (mnn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    ArrayList<Attack> attackss = mnn.getAllowedAttacks();
                    Attack accs = attackss.get(Integer.parseInt(args[2]) + 1);
                    switch (args[3].toLowerCase()) {
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

                case "delete":
                case "del":
                case "trash":
                    Monster mnnn = user.getMonsters().get(Integer.parseInt(args[1]) - 1);
                    if (mnnn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    user.getMonsters().remove(mnnn);
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.deleted", user.getLang(), null), event.getChannel());
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
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
        return engine.lang("cmd.pokemon.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }
}
