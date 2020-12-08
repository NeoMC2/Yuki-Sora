package botApplication.discApplication.librarys.dungeon.queue;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.Serializable;

public class DungeonChannelHandler implements Serializable {

    private static final long serialVersionUID = 42L;

    private String channelId = "";
    private String roleId = "";

    public DungeonChannelHandler(String channelId, String roleId) {
        this.channelId = channelId;
        this.roleId = roleId;
    }

    public void clicked(Engine engine, Guild g, Member member) {
        DiscApplicationUser usr = engine.getDiscEngine().getFilesHandler().getUserById(member.getId());
        g.addRoleToMember(member, g.getRoleById(roleId)).queue();
        engine.getDiscEngine().getTextUtils().sendWarining("Type in the ID of the Monster you want to go into the dungeon!", g.getTextChannelById(channelId));
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                Monster m;
                try {
                    m = usr.getMonsters().get(Integer.parseInt(respondingEvent.getMessage().getContentRaw()) - 1);
                } catch (Exception e) {
                    engine.getDiscEngine().getTextUtils().sendError("This Monster is invalid! Aborting!", respondingEvent.getChannel(), false);
                    engine.getDiscEngine().getFilesHandler().getServerById(g.getId()).getDungeonQueueHandler().unuseChannel(channelId, respondingEvent.getGuild());
                    return;
                }
                Dungeon d = new Dungeon(member, g.getTextChannelById(channelId), g, engine, usr, m, engine.getDiscEngine().getFilesHandler().getServerById(g.getId()));
                engine.getDiscEngine().getFilesHandler().getDungeons().put(member.getId(), d);
                d.start();
            }
        };
        r.discGuildId = g.getId();
        r.discChannelId = channelId;
        r.discUserId = member.getId();
        engine.getResponseHandler().makeResponse(r);
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