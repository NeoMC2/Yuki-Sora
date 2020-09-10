package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.consumable.food.Food;
import botApplication.discApplication.librarys.item.monsters.Attack;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.librarys.item.monsters.Monster;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;

public class DiscCmdMonster implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {
            Monster mn = null;
            if (args.length >= 2)
                try {
                    mn = user.getMonsters().get(Integer.parseInt(args[1]) - 1);
                } catch (Exception e) {
                }
            switch (args[0].toLowerCase()) {
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
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    for (FightHandler ha : engine.getDiscEngine().getFightHandlers()) {
                        if (ha.getTextChannel().getId().equals(event.getChannel().getId())) {
                            return;
                        }
                    }
                    mn.setHp(mn.getMaxHp());
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.healed", user.getLang(), null), event.getChannel());
                    break;

                case "buy":
                    if (user.getCoins() >= 20) {
                        user.substractCoins(20);
                        Monster monster = engine.getDiscEngine().getTransaktionHandler().getRandomMonster(Item.Rarity.Normal);
                        if (monster == null) {
                            engine.getDiscEngine().getTextUtils().sendError("An error showed up, you'll get your coins back. Try again soon!", event.getChannel(), false);
                            return;
                        }
                        EmbedBuilder mb = new EmbedBuilder()
                                .setDescription(engine.lang("cmd.pokemon.success.buy", user.getLang(), new String[]{monster.getItemName(), Item.rarityToString(monster.getItemRarity())}))
                                .setAuthor("Got " + monster.getItemName(), monster.getImgUrl())
                                .setColor(Item.rarityToColor(monster.getItemRarity()))
                                .setThumbnail(monster.getImgUrl());
                        event.getChannel().sendMessage(mb.build()).queue();
                        try {
                            user.addMonster(monster);
                        } catch (Exception e) {
                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.pokemon.error.toManyPokemons", user.getLang(), null), event.getChannel(), false);
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.wallet.error.notEnoughMoney", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "list":
                case "info":
                case "show":
                case "monsters":
                    String msg = "Monsters\n\n";
                    if (args.length > 1) {
                        int i = Integer.parseInt(args[1]);
                        try {
                            msg = "[" + i + "]\n" + user.getMonsters().get(i - 1).toString();
                        } catch (Exception e) {
                            engine.getDiscEngine().getTextUtils().sendError("Invalid", event.getChannel(), false);
                            if(engine.getProperties().debug)
                                e.printStackTrace();
                            return;
                        }
                    } else {
                        for (int i = 0; i < user.getMonsters().size(); i++) {
                            msg += "[" + (i + 1) + "] " + user.getMonsters().get(i).getItemName() + "\n";
                        }
                    }
                    engine.getDiscEngine().getTextUtils().sendWarining(msg, event.getChannel());
                    break;

                case "ai":
                case "attackinfo":
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid", event.getChannel(), false);
                        return;
                    }
                    String msgg = "";
                    ArrayList<Attack> attacks = mn.getAllowedAttacks();
                    for (int i = 0; i < attacks.size(); i++) {
                        Attack c = attacks.get(i);
                        msgg += "[" + (i + 1) + "] " + c.toString() + "\n\n";
                    }
                    engine.getDiscEngine().getTextUtils().sendWarining(msgg, event.getChannel());
                    break;

                case "attackselect":
                case "selectattack":
                case "as":
                case "sa":
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    ArrayList<Attack> attackss = mn.getAllowedAttacks();
                    Attack accs = attackss.get(Integer.parseInt(args[2]) - 1);
                    switch (args[3].toLowerCase()) {
                        case "a1":
                            mn.setA1(accs);
                            break;
                        case "a2":
                            mn.setA2(accs);
                            break;
                        case "a3":
                            mn.setA3(accs);
                            break;
                        case "a4":
                            mn.setA4(accs);
                            break;
                    }
                    engine.getDiscEngine().getTextUtils().sendSucces("Selected!", event.getChannel());
                    break;

                case "delete":
                case "del":
                case "trash":
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    user.getMonsters().remove(mn);
                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.pokemon.success.deleted", user.getLang(), null), event.getChannel());
                    break;

                case "sell":
                    if (mn == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Monster", event.getChannel(), false);
                        return;
                    }
                    user.getMonsters().remove(mn);
                    user.addCoins(Monster.rarityToMarketValue(mn.getItemRarity()));
                    engine.getDiscEngine().getTextUtils().sendSucces("You've got " + Monster.rarityToMarketValue(mn.getItemRarity()) + " for your monster", event.getChannel());
                    break;

                case "feed":
                    Food f;
                    try {
                        f = (Food) user.getItems().get(Integer.parseInt(args[2])-1);
                    } catch (Exception e) {
                        engine.getDiscEngine().getTextUtils().sendError("Invalid Food!", event.getChannel(), false);
                        return;
                    }
                    user.getItems().remove(f);
                    mn.feed(f);
                    EmbedBuilder b = new EmbedBuilder().setAuthor("Feed " + mn.getItemName(), null, f.getImgUrl()).setDescription(engine.lang("cmd.pokemon.success.feed", user.getLang(), null));
                    event.getChannel().sendMessage(b.build()).queue();
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
