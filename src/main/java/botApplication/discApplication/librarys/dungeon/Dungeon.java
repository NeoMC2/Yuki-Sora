package botApplication.discApplication.librarys.dungeon;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.parts.Cave;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Dungeon {

    private final String cave1 =
            "╔════╗\n" +
                    "╝▒▒▒▒╚\n" +
                    "╗▒▒▒▒╔\n" +
                    "╚════╝";
    private final String cave2 =
            "╔═╝╚═╗\n" +
                    "╝▒▒▒▒╚\n" +
                    "╗▒▒▒▒╔\n" +
                    "╚════╝";
    private final String cave3 =
            "╔═╝╚═╗\n" +
                    "╝▒▒▒▒╚\n" +
                    "╗▒▒▒▒╔\n" +
                    "╚═╗╔═╝";
    private final String cave0 =
            "╔════╗\n" +
                    "╝▒▒▒▒║\n" +
                    "╗▒▒▒▒║\n" +
                    "╚════╝";
    private Cave currentCave;
    private final Member member;
    private final TextChannel textChannel;
    private final Guild g;
    private final Engine engine;
    private final DiscApplicationUser user;
    private String m;
    private final DiscApplicationServer server;
    private final DungeonGenerator dungeonGenerator;

    public Dungeon(Member member, TextChannel textChannel, Guild g, Engine engine, DiscApplicationUser user, String m, DiscApplicationServer server) {
        this.member = member;
        this.textChannel = textChannel;
        this.g = g;
        this.engine = engine;
        this.user = user;
        this.m = m;
        this.server = server;
        dungeonGenerator = new DungeonGenerator(engine);
    }

    private void sendCaveInfo(String s, int junc) {
        String junction = "";
        if (junc == 0)
            junction = cave0;
        else if (junc == 1)
            junction = cave1;
        else if (junc == 2)
            junction = cave2;
        else if (junc == 3)
            junction = cave3;

        EmbedBuilder b = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setDescription(s + "\n\n" + junction + "");
        textChannel.sendMessage(b.build()).queue();
    }

    public void caveActionFinished(boolean returnToHome) {
        if (returnToHome) {
            String coll = "";
            engine.getDiscEngine().getTextUtils().sendSucces("You are done with this Dungeon, congrats!", textChannel);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    server.getDungeonQueueHandler().unuseChannel(textChannel.getId(), g);
                }
            }, 10000);
            return;
        }
        if (currentCave.junctions() == 0) {
            String m = "It seems like you are at the end of this cave! If you want to go back you can write `back`!";
            sendCaveInfo(m, 0);
        } else {
            String m = "You are done with this cave, you can choose between \n" +
                    +currentCave.junctions() + " ways! Which one you are going to take? (write 1, 2, 3, 4 etc.)\"";
            sendCaveInfo(m, currentCave.junctions());
        }
        Dungeon d = this;
        Response r = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                String msg = respondingEvent.getMessage().getContentRaw().toLowerCase();
                if (msg.equals("end") || msg.equals("leave") || msg.equals("stop")) {
                    caveActionFinished(true);
                    return;
                }
                if (msg.equals("back")) {
                    if (currentCave.getGoneBefore() == null) {
                        engine.getDiscEngine().getTextUtils().sendError("Seems like, this is the start of the dungeon! If you want to leave the dungeon you can use end!", textChannel, false);
                    } else {
                        currentCave = currentCave.getGoneBefore();
                        currentCave.enter(null);
                    }
                    return;
                }
                int i = 0;
                try {
                    i = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                } catch (Exception e) {
                    engine.getDiscEngine().getTextUtils().sendError("This junctions is invalid!", textChannel, false);
                    caveActionFinished(false);
                    return;
                }
                Cave before = currentCave;
                try {
                    currentCave = currentCave.goFurther(i);
                } catch (Exception e) {
                    engine.getDiscEngine().getTextUtils().sendError("This junctions is invalid!", textChannel, false);
                    caveActionFinished(false);
                    return;
                }

                currentCave.init(d);
                currentCave.enter(before);
            }
        };
        r.discUserId = member.getId();
        r.discChannelId = textChannel.getId();
        r.discGuildId = g.getId();
        engine.getResponseHandler().makeResponse(r);
    }

    public Member getMember() {
        return member;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public Guild getG() {
        return g;
    }

    public Engine getEngine() {
        return engine;
    }

    public DiscApplicationUser getUser() {
        return user;
    }

    public void start() {
        currentCave = dungeonGenerator.generateDungeon(this);
        currentCave.enter(null);
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }
}
