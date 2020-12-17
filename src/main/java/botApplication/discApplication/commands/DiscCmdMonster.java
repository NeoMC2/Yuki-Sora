package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.FightHandler;
import botApplication.discApplication.utils.DiscUtilityBase;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
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

                case "buy":
                    user.substractCoins(20);
                    JSONObject res = engine.getDiscEngine().getApiManager().userRandomMonster(user.getUserId(), "normal");
                    JSONObject mnster = (JSONObject) res.get("data");
                    String mnsterName = (String) mnster.get("name");
                    String rar = (String) mnster.get("rarity");
                    String imgUrl = (String) mnster.get("imageUrl");

                    EmbedBuilder b = new EmbedBuilder().setThumbnail(imgUrl).setColor(DiscCmdItem.rarityToColor(rar)).setAuthor("You've got " + mnsterName);
                    event.getChannel().sendMessage(b.build()).queue();
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
                    FightBuilder fBuilder = new FightBuilder(fi.getId(), event.getAuthor().getId());

                    Response r = firstRes(fBuilder, mn1, engine, event.getChannel());
                    r.discGuildId = event.getGuild().getId();
                    r.discChannelId = event.getChannel().getId();
                    r.discUserId = event.getAuthor().getId();
                    engine.getResponseHandler().makeResponse(r);


                    JSONObject m2Req = engine.getDiscEngine().getApiManager().getUserMonstersById(event.getAuthor().getId());
                    JSONArray mn2 = (JSONArray) m2Req.get("data");
                    String m2S = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mn2);
                    engine.getDiscEngine().getTextUtils().sendSucces(fi.getEffectiveName() + " Monsterlist:\n\n" + m2S, event.getChannel());

                    Response rr = firstRes(fBuilder, mn2, engine, event.getChannel());
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

    private Response firstRes(FightBuilder fightBuilder, JSONArray mnsters, Engine engine, TextChannel textChannel) {
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                try {
                    String m = (String) ((JSONObject) mnsters.get(id)).get("_id");
                    fightBuilder.choose(respondingEvent.getAuthor().getId(), m);
                    engine.getDiscEngine().getTextUtils().sendSucces("You've chosen " + (String) ((JSONObject) mnsters.get(id)).get("name"), textChannel);
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
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                respondingEvent.getChannel().sendMessage(fightHandler.round(respondingEvent.getMessage().getContentRaw())).queue();
                if (!fightHandler.fightDone) {
                    createResponse(engine, fightHandler.nextPlayer(), chanId, guildId, fightHandler);
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

        public FightBuilder(String m1, String m2) {
            this.m1 = m1;
            this.m2 = m2;
        }

        public void choose(String id, String monster) {
            if (id.equals(m1)) {
                m1Choose = true;
                m1M = monster;
            }


            if (id.equals(m2)) {
                m2Choose = true;
                m2M = monster;
            }
        }

        public boolean allChoose() {
            return m1Choose && m2Choose;
        }
    }
}
