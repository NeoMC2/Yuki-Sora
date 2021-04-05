package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.FightHandler;
import botApplication.discApplication.utils.DiscUtilityBase;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DiscCmdMonster implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {

            switch (args[0].toLowerCase()) {
                case "heal":
                    heal(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "info":
                    info(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "feed":
                    feed(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "delete":
                    delete(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "list":
                    list(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "attackinfo":
                    attackInfo(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;

                case "attack":
                    if(args.length>=2)
                        if(args[1].equals("info"))
                            attackInfo(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                    break;

                case "selectattack":
                    selectAttack(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                break;
                
                case "select":
                    if(args.length>=2)
                        if(args[1].equals("attack"))
                            selectAttack(engine, event.getAuthor(), event.getGuild(), event.getChannel());
                    break;

                case "fight":
                    Member fi = null;
                    try {
                        fi = event.getMessage().getMentionedMembers().get(0);
                    } catch (Exception ignored) {
                    }

                    if (fi == null) {
                        if (args.length > 1)
                            fi = event.getGuild().getMemberById(args[1]);
                        else {
                            engine.getDiscEngine().getTextUtils().sendError("No user found!", event.getChannel(), false);
                            return;
                        }
                    }

                    if (fi == null) {
                        engine.getDiscEngine().getTextUtils().sendError("No user found!", event.getChannel(), false);
                        return;
                    }
                    JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(event.getAuthor().getId());
                    JSONArray mn1 = (JSONArray) m1Req.get("data");
                    String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
                    engine.getDiscEngine().getTextUtils().sendSucces(event.getAuthor().getName() + " Monsterlist:\n\n" + m1S, event.getChannel());

                    JSONObject m2Req = engine.getDiscEngine().getApiManager().getUserMonstersById(fi.getId());
                    JSONArray mn2 = (JSONArray) m2Req.get("data");
                    String m2S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn2);
                    engine.getDiscEngine().getTextUtils().sendSucces(fi.getEffectiveName() + " Monsterlist:\n\n" + m2S, event.getChannel());

                    FightBuilder fBuilder = new FightBuilder(event.getAuthor().getId(), fi.getId(), mn1, mn2);

                    Response r = firstRes(fBuilder, engine, event.getChannel());
                    r.discGuildId = event.getGuild().getId();
                    r.discChannelId = event.getChannel().getId();
                    r.discUserId = event.getAuthor().getId();
                    engine.getResponseHandler().makeResponse(r);

                    Response rr = firstRes(fBuilder, engine, event.getChannel());
                    rr.discGuildId = event.getGuild().getId();
                    rr.discChannelId = event.getChannel().getId();
                    rr.discUserId = fi.getId();
                    engine.getResponseHandler().makeResponse(rr);
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
            }
        }
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        if(args.length >=1 )
        switch (args[0]){
            case "heal":
                heal(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "info":
                info(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "feed":
                feed(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "delete":
                delete(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "list":
                list(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "attackinfo":
                attackInfo(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "attack":
                if(args.length>=2)
                    if(args[1].equals("info"))
                        attackInfo(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "selectattack":
                selectAttack(engine, event.getAuthor(), null, event.getChannel());
                break;

            case "select":
                if(args.length>=2)
                    if(args[1].equals("attack"))
                        selectAttack(engine, event.getAuthor(), null, event.getChannel());
                break;
        }
    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.pokemon.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private Response firstRes(FightBuilder fightBuilder, Engine engine, TextChannel textChannel) {
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                try {

                    fightBuilder.choose(respondingEvent.getAuthor().getId(), id);
                    //engine.getDiscEngine().getTextUtils().sendSucces("You've chosen " + (String) ((JSONObject) mnsters.get(id)).get("name"), textChannel);
                    if (fightBuilder.allChoose()) {
                        FightHandler h = new FightHandler(fightBuilder.m1, fightBuilder.m2, fightBuilder.m1M, fightBuilder.m2M, engine);
                        createResponse(engine, h.nextPlayer(), respondingEvent.getChannel().getId(), respondingEvent.getGuild().getId(), h);
                    }
                } catch (Exception e) {
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                    engine.getDiscEngine().getTextUtils().sendError("Error while starting fight", textChannel, true);
                }
            }
        };
        return r;
    }

    private void createResponse(Engine engine, String userId, String chanId, String guildId, FightHandler fightHandler) {
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                respondingEvent.getChannel().sendMessage(fightHandler.round(respondingEvent.getMessage().getContentRaw())).queue();
                if (!fightHandler.fightDone) {
                    createResponse(engine, fightHandler.nextPlayer(), chanId, guildId, fightHandler);
                } else {
                    JSONObject winner = (JSONObject) engine.getDiscEngine().getApiManager().getUserBy(fightHandler.getWinner()).get("data");
                    engine.getDiscEngine().getTextUtils().sendSucces(winner.get("username") + " won the match!", respondingEvent.getChannel());
                }
            }
        };
        r.discUserId = userId;
        r.discChannelId = chanId;
        r.discGuildId = guildId;
        engine.getResponseHandler().makeResponse(r);
    }

    private class FightBuilder {
        public boolean m1Choose = false;
        public boolean m2Choose = false;

        public String m1;
        public String m2;

        public String m1M;
        public String m2M;

        public JSONArray m1Mns;
        public JSONArray m2Mns;

        public FightBuilder(String m1, String m2, JSONArray m1Mns, JSONArray m2Mns) {
            this.m1 = m1;
            this.m2 = m2;
            this.m1Mns = m1Mns;
            this.m2Mns = m2Mns;
        }

        public void choose(String id, int monster) {
            if (id.equals(m1)) {
                m1Choose = true;
                m1M = (String) ((JSONObject) m1Mns.get(monster)).get("_id");
            }


            if (id.equals(m2)) {
                m2Choose = true;
                m2M = (String) ((JSONObject) m2Mns.get(monster)).get("_id");
            }
        }

        public boolean allChoose() {
            return m1Choose && m2Choose;
        }
    }

    private void heal(Engine engine, User user, Guild g, MessageChannel channel) {
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                engine.getDiscEngine().getApiManager().healMonster((String) monster.get("_id"));

                engine.getDiscEngine().getTextUtils().sendSucces("Healed monster!", channel);
            }
        };
        r.discUserId = user.getId();
        if (g != null)
            r.discGuildId = g.getId();
        r.discChannelId = channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    private void info(Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                JSONObject root = null;
                JSONArray roots = (JSONArray) engine.getDiscEngine().getApiManager().getMonsters().get("data");
                for (Object ob : roots) {
                    JSONObject obj1 = (JSONObject) ob;
                    if (monster.get("rootMonster").equals(obj1.get("_id"))) {
                        root = obj1;
                    }
                }
                EmbedBuilder b = new EmbedBuilder().setDescription(DiscUtilityBase.getMonsterInfo(engine, monster)).setAuthor((String) root.get("name")).setColor(DiscCmdItem.rarityToColor((String) root.get("rarity"))).setThumbnail((String) root.get("imageUrl"));
                respondingEvent.getChannel().sendMessage(b.build()).queue();
            }
        };
        r.discUserId = user.getId();
        if(g != null)
        r.discGuildId = g.getId();
        r.discChannelId = channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    private void delete (Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                engine.getDiscEngine().getApiManager().removeUserMonster((String) monster.get("_id"));
                engine.getDiscEngine().getTextUtils().sendSucces("Deleted Monster from your inventory!", respondingEvent.getChannel());
            }
        };
        r.discUserId = user.getId();
        if(g != null)
        r.discGuildId = g.getId();
        r.discChannelId =channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    private void feed(Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                JSONObject res = engine.getDiscEngine().getApiManager().removeCoinsFromUser(respondingEvent.getAuthor().getId(), 5);
                if (((Long) res.get("status") == 200)) {
                    engine.getDiscEngine().getApiManager().feedMonster((String) monster.get("_id"));
                    engine.getDiscEngine().getTextUtils().sendSucces("Successfully fed monster", respondingEvent.getChannel());
                } else {
                    engine.getDiscEngine().getTextUtils().sendError("Can't feed monster", respondingEvent.getChannel(), false);
                }
            }
        };
        r.discUserId = user.getId();
        if(g != null)
        r.discGuildId = g.getId();
        r.discChannelId = channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    private void list(Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Monsterlist:\n\n" + m1S, channel);
    }

    private void attackInfo(Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                JSONObject at = engine.getDiscEngine().getApiManager().getAttacksByUserMonster((String) monster.get("_id"));
                JSONArray attacks = (JSONArray) at.get("data");
                String s = "Available attacks:\n" + DiscUtilityBase.getAttacksListFromAttackList(engine, attacks);
                engine.getDiscEngine().getTextUtils().sendSucces(s, respondingEvent.getChannel());
            }
        };
        r.discUserId = user.getId();
        if(g != null)
        r.discGuildId = g.getId();
        r.discChannelId = channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }
    
    private void selectAttack(Engine engine, User user, Guild g, MessageChannel channel){
        JSONObject m1Req = engine.getDiscEngine().getApiManager().getUserMonstersById(user.getId());
        JSONArray mn1 = (JSONArray) m1Req.get("data");
        String m1S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn1);
        engine.getDiscEngine().getTextUtils().sendSucces("Select one of your monsters\nMonsterlist:\n\n" + m1S, channel);
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                JSONObject monster = (JSONObject) mn1.get(id);
                JSONObject at = engine.getDiscEngine().getApiManager().getAttacksByUserMonster((String) monster.get("_id"));
                JSONArray attacks = (JSONArray) at.get("data");
                String s = "Select one of the available attacks\n\nAvailable attacks:\n" + DiscUtilityBase.getAttacksListFromAttackList(engine, attacks);
                engine.getDiscEngine().getTextUtils().sendSucces(s, respondingEvent.getChannel());

                Response rr = new Response(ResponseTyp.Discord) {
                    @Override
                    public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                        int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                        JSONObject sAttack = (JSONObject) attacks.get(id);
                        engine.getDiscEngine().getTextUtils().sendSucces("Set attack to one of your attack slots(a1,a2,a3,a4)", respondingEvent.getChannel());
                        Response rrr = new Response(ResponseTyp.Discord) {
                            @Override
                            public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                                switch (respondingEvent.getMessage().getContentRaw().toLowerCase()) {
                                    case "a1":
                                        engine.getDiscEngine().getApiManager().giveMonsterAttack((String) monster.get("_id"), "a1", (String) sAttack.get("_id"));
                                        break;

                                    case "a2":
                                        engine.getDiscEngine().getApiManager().giveMonsterAttack((String) monster.get("_id"), "a2", (String) sAttack.get("_id"));
                                        break;

                                    case "a3":
                                        engine.getDiscEngine().getApiManager().giveMonsterAttack((String) monster.get("_id"), "a3", (String) sAttack.get("_id"));
                                        break;

                                    case "a4":
                                        engine.getDiscEngine().getApiManager().giveMonsterAttack((String) monster.get("_id"), "a4", (String) sAttack.get("_id"));
                                        break;
                                }
                                engine.getDiscEngine().getTextUtils().sendSucces("Set attack!", respondingEvent.getChannel());
                            }
                        };
                        rrr.discUserId = user.getId();
                        if(g != null)
                            rrr.discGuildId = g.getId();
                        rrr.discChannelId = channel.getId();
                        engine.getResponseHandler().makeResponse(rrr);


                    }
                };
                rr.discUserId = user.getId();
                if(g != null)
                    rr.discGuildId = g.getId();
                rr.discChannelId = channel.getId();
                engine.getResponseHandler().makeResponse(rr);
            }
        };
        r.discUserId = user.getId();
        if(g != null)
            r.discGuildId = g.getId();
        r.discChannelId = channel.getId();
        engine.getResponseHandler().makeResponse(r);
    }
}