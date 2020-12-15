package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.FightHandler;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.Serializable;

public class MonsterFight implements DungeonAction, Serializable {

    public Engine engine;
    public Dungeon d;
    private final String msg = "Seems like here is a monster!";
    private FightHandler fightHandler;

    public MonsterFight(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        fightHandler = new FightHandler(d.getUser().getUserId(), null, d.getM(), null, engine);
        createResponse(engine, dungeon.getUser().getUserId(), dungeon.getTextChannel().getId(), dungeon.getTextChannel().getGuild().getId());
    }

    @Override
    public void generate() {

    }

    private void createResponse(Engine engine, String userId, String chanId, String guildId){
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                respondingEvent.getChannel().sendMessage(fightHandler.round(respondingEvent.getMessage().getContentRaw())).queue();
                if(!fightHandler.fightDone){
                    createResponse(engine, userId, chanId, guildId);
                }
            }
        };
        r.discUserId = userId;
        r.discChannelId = chanId;
        r.discGuildId = guildId;
        engine.getResponseHandler().makeResponse(r);
    }
}
