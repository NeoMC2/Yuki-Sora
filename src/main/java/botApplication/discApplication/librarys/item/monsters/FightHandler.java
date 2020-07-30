package botApplication.discApplication.librarys.item.monsters;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class FightHandler {

    private Engine engine;

    private Monster monsterM1;
    private Monster monsterM2;
    private Member m1;
    private Member m2;

    private TextChannel textChannel;
    private final Guild guild;

    private boolean m1Choose = false;
    private boolean m2Choose = false;

    private int turn = 2;

    private Monster m = null;
    private Monster e = null;
    private Attack a = null;
    private Member turner = null;
    private Member enemy = null;

    public FightHandler(Engine engine, TextChannel textChannel, Guild g) {
        this.engine = engine;
        this.textChannel = textChannel;
        this.guild = g;
    }

    public void begin() {
        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.pokemon.info.fighStart", "en", new String[]{m1.getUser().getName(), m2.getUser().getName()}), textChannel, "Fight", Color.MAGENTA);
        chooseMonster();
    }

    private void chooseMonster() {
        engine.getDiscEngine().getTextUtils().sendCustomMessage("Its now time to choose your attacking Monster's by writing down the id of your Monster!", textChannel, "Fight", Color.MAGENTA);

        Response r1 = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                memberChooseMonster(respondingEvent);
            }
        };
        r1.discGuildId = guild.getId();
        r1.discChannelId = textChannel.getId();
        r1.discUserId = m1.getUser().getId();
        engine.getResponseHandler().makeResponse(r1);

        Response r2 = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                memberChooseMonster(respondingEvent);
            }
        };
        r2.discGuildId = guild.getId();
        r2.discChannelId = textChannel.getId();
        r2.discUserId = m2.getUser().getId();
        engine.getResponseHandler().makeResponse(r2);
    }

    private void memberChooseMonster(GuildMessageReceivedEvent event) {
        DiscApplicationUser usr;
        try {
            usr = engine.getDiscEngine().getFilesHandler().getUserById(event.getAuthor().getId());
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("You Userdata can't found! Game abort!", textChannel, false);
            engine.getDiscEngine().getFightHandlers().remove(this);
            return;
        }
        if (event.getAuthor().getId().equals(m1.getUser().getId())) {
            try {
                monsterM1 = usr.getMonsters().get(Integer.parseInt(event.getMessage().getContentRaw()) - 1);
                engine.getDiscEngine().getTextUtils().sendSucces("**Monster**\n\n" + monsterM1.toString(), event.getChannel());
                if (monsterM1 == null)
                    throw new Exception();
                m1Choose = true;
            } catch (Exception e) {
                engine.getDiscEngine().getTextUtils().sendError("This Monster is invalid! Aborting!", textChannel, false);
                engine.getDiscEngine().getFightHandlers().remove(this);
            }
        } else {
            try {
                monsterM2 = usr.getMonsters().get(Integer.parseInt(event.getMessage().getContentRaw()) - 1);
                engine.getDiscEngine().getTextUtils().sendSucces("**Monster**\n\n" + monsterM2.toString(), event.getChannel());
                if (monsterM2 == null)
                    throw new Exception();
                m2Choose = true;
            } catch (Exception e) {
                engine.getDiscEngine().getTextUtils().sendError("This Monster is invalid! Aborting!", textChannel, false);
                engine.getDiscEngine().getFightHandlers().remove(this);
            }
        }
        if (m1Choose && m2Choose)
            round();
    }

    private void round() {
        Member turner = null;
        Member enemy = null;
        if (turn == 2) {
            turner = m2;
            enemy = m1;
            engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.pokemon.info.turn", "en", new String[]{m2.getUser().getName(), m2.getUser().getName()}), textChannel, "Turn", Color.MAGENTA);
        } else if (turn == 1) {
            turner = m1;
            enemy = m2;
            engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.pokemon.info.turn", "en", new String[]{m1.getUser().getName(), m2.getUser().getName()}), textChannel, "Turn", Color.MAGENTA);
        }

        Member finalTurner = turner;
        Member finalEnemy = enemy;
        Response gamerResponse = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                if (turn == 1) {
                    m = monsterM1;
                    e = monsterM2;
                } else {
                    m = monsterM2;
                    e = monsterM1;
                }
                switch (respondingEvent.getMessage().getContentDisplay()) {
                    case "a1":
                        a = m.getA1();
                        attack(finalTurner, finalEnemy);
                        break;

                    case "a2":
                        a = m.getA2();
                        attack(finalTurner, finalEnemy);
                        break;

                    case "a3":
                        a = m.getA3();
                        attack(finalTurner, finalEnemy);
                        break;

                    case "a4":
                        a = m.getA4();
                       attack(finalTurner, finalEnemy);
                        break;

                    case "help":
                        String msg = "a1 - Use attack 1\n a2 - Use attack 2\na3 - Use attack 3\na4 - Use attack 4\ninfo - shows stats of your Monster\nend - stops the fight";
                        engine.getDiscEngine().getTextUtils().sendWarining(msg, textChannel);
                        round();
                        break;

                    case "info":
                        String sa1, sa2, sa3, sa4;
                        try {
                            sa1 = m.getA1().toString() + "\n";
                        } catch (Exception ex) {
                            sa1 = "not selected";
                        }

                        try {
                            sa2 = m.getA2().toString() + "\n";
                        } catch (Exception ex) {
                            sa2 = "not selected";
                        }

                        try {
                            sa3 = m.getA3().toString() + "\n";
                        } catch (Exception ex) {
                            sa3 = "not selected";
                        }

                        try {
                            sa4 = m.getA4().toString()+ "\n";
                        } catch (Exception ex) {
                            sa4 = "not selected";
                        }

                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.pokemon.info.pokemonInfo", "en", new String[]{m.getItemName(), String.valueOf(m.getHp()), sa1, sa2, sa3, sa4}), textChannel, "Monster Info", Color.YELLOW);
                        round();
                        break;

                    case "end":
                    case "stop":
                        engine.getDiscEngine().getTextUtils().sendWarining("Fight stopped!", textChannel);
                        engine.getDiscEngine().getFightHandlers().remove(this);
                        break;

                    default:
                        engine.getDiscEngine().getTextUtils().sendWarining("invalid!", textChannel);
                        round();
                        break;
                }
            }
        };
        gamerResponse.discGuildId = guild.getId();
        gamerResponse.discChannelId = textChannel.getId();
        gamerResponse.discUserId = turner.getUser().getId();
        engine.getResponseHandler().makeResponse(gamerResponse);
    }

    private void attack(Member user, Member enemy){
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().getUserById(user.getId());
        if (isAttackValid(a, textChannel)) {
            showAttackInfo(m, e, m.attack(m, a, e), a);
            a.use();
            if (testWinner(e)) {
                m.earnXP(10, engine, usr);
                e.earnXP(3, engine, usr);
                printWinner(user, enemy);
                return;
            }
            makeNewRound();
        }
        round();
    }

    private boolean isAttackValid(Attack a, TextChannel c) {
        if (a == null) {
            engine.getDiscEngine().getTextUtils().sendError("Ivalid attack!", c, false);
            return false;
        }

        if(a.getUsed() <= 0){
            engine.getDiscEngine().getTextUtils().sendError("This attack can't be used anymore!", c, false);
            return false;
        }
        return true;
    }

    private void showAttackInfo(Monster own, Monster enemy, int dmg, Attack attack) {
        String msg = own.getItemName() + " attacked " + enemy.getItemName() + " with " + attack.getAttackName() + " and made " + dmg + " damage. " + enemy.getItemName() + " has " + enemy.getHp() + " HP left!";
        EmbedBuilder e = new EmbedBuilder()
                .setDescription(msg)
                .setColor(Color.YELLOW)
                .setImage(own.getImgUrl())
                .setTitle(own.getItemName() + " against " + enemy.getItemName());
        textChannel.sendMessage(e.build()).queue();
    }

    private boolean testWinner(Monster enemy) {
        return enemy.getHp() == 0;
    }

    private void printWinner(Member winner, Member looser) {
        String msg = winner.getUser().getName() + " has won the match because the enemy Monster was to weak, congratulations! Your monster got 20 xp!";
        engine.getDiscEngine().getTextUtils().sendSucces(msg, textChannel);
        engine.getDiscEngine().getFightHandlers().remove(this);
    }

    private void makeNewRound() {
        if (turn == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Member getM1() {
        return m1;
    }

    public void setM1(Member m1) {
        this.m1 = m1;
    }

    public Member getM2() {
        return m2;
    }

    public void setM2(Member m2) {
        this.m2 = m2;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
}