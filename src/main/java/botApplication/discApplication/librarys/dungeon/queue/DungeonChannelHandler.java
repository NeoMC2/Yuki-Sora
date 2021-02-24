package botApplication.discApplication.librarys.dungeon.queue;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.utils.DiscUtilityBase;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class DungeonChannelHandler implements Serializable {

    private static final long serialVersionUID = 42L;

    private String channelId = "";
    private String roleId = "";

    public DungeonChannelHandler(String channelId, String roleId) {
        this.channelId = channelId;
        this.roleId = roleId;
    }

    public void clicked(Engine engine, Guild g, Member member, DiscApplicationUser user, DiscApplicationServer server) {
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().getUserById(member.getId());
        g.addRoleToMember(member, g.getRoleById(roleId)).queue();
        JSONObject res = engine.getDiscEngine().getApiManager().getUserMonstersById(member.getId());
        JSONArray mnsters = (JSONArray) res.get("data");
        String s = DiscUtilityBase.getMonsterListFromUserMonsters(engine, mnsters);

        engine.getDiscEngine().getTextUtils().sendSucces(s, g.getTextChannelById(channelId));
        engine.getDiscEngine().getTextUtils().sendWarining("Type in the ID of the Monster you want to go into the dungeon!", g.getTextChannelById(channelId));
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                int id = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                try {
                    String m = (String)((JSONObject) mnsters.get(id)).get("_id");
                    int hp = getNumber((JSONObject) mnsters.get(id), "hp");
                    if(hp <= 0){
                        engine.getDiscEngine().getTextUtils().sendError("Your monster is exhausted, maybe you should heal it first!", g.getTextChannelById(channelId), false);
                        user.setLastDungeonVisit(null);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                server.getDungeonQueueHandler().unuseChannel(channelId, g);
                            }
                        }, 8 * 10 * 10 * 10 );
                        return;
                    }
                    Dungeon d = new Dungeon(member, g.getTextChannelById(channelId), g, engine, usr, m, engine.getDiscEngine().getFilesHandler().getServerById(g.getId()));
                    engine.getDiscEngine().getFilesHandler().getDungeons().put(member.getId(), d);
                    d.start();
                } catch (Exception e){
                    engine.getDiscEngine().getTextUtils().sendError("Error while starting dungeon", g.getTextChannelById(channelId), true);
                    user.setLastDungeonVisit(null);
                    server.getDungeonQueueHandler().unuseChannel(channelId, g);
                }
            }
        };
        r.discGuildId = g.getId();
        r.discChannelId = channelId;
        r.discUserId = member.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    private int getNumber(JSONObject o, String r) {
        try {
            return Math.toIntExact((Long) o.get(r));
        } catch (Exception e) {
            return Math.toIntExact(Math.round((Double) o.get(r)));
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


}