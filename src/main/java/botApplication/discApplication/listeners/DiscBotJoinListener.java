package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.certification.DiscCertificationLevel;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class DiscBotJoinListener extends ListenerAdapter {

    private final Engine engine;

    public DiscBotJoinListener(Engine engine) {
        this.engine = engine;
    }


    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            engine.getDiscEngine().getTextUtils().sendCustomMessage("こんにちは、私の名前は雪空\n\nHallo, ich bin Yuki Sora :3\n\nBenutze `-setup` um mit meiner Einrichtung zu beginnen!", event.getGuild().getTextChannels().get(0), "こんにちは", Color.YELLOW);
        } catch (Exception e) {
            e.printStackTrace();
            engine.getUtilityBase().printOutput("[DISCORD - on Guild join] Can't send wellcome Message...idk why", true);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        DiscApplicationUser u = engine.getDiscEngine().getFilesHandler().getUserById(event.getMember().getUser().getId());

        if (s == null)
            return;
        if (s.getWelcomeText() == null || s.getWelcomeMessageChannel() == null)
            return;
        if (u == null) {
            u = engine.getDiscEngine().getFilesHandler().createNewUser(event.getUser(), DiscCertificationLevel.Member);
        }
        if (!u.isSaidHello()) {
            if (s.getWelcomeMessageChannel() != null && s.getWelcomeText() != null) {
                EmbedBuilder b = new EmbedBuilder()
                        .setDescription(s.getWelcomeText())
                        .setColor(Color.ORANGE)
                        .setAuthor(event.getMember().getUser().getName() + " joined our Guild!", null, event.getMember().getUser().getAvatarUrl());
                event.getGuild().getTextChannelById(s.getWelcomeMessageChannel()).sendMessage(b.build()).queue();
                s.updateServerStats(engine);
            }
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        DiscApplicationServer s = engine.getDiscEngine().getFilesHandler().getServerById(event.getGuild().getId());
        if (s == null)
            return;

        s.updateServerStats(engine);
    }
}