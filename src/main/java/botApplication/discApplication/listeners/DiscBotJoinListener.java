package botApplication.discApplication.listeners;

import core.Engine;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class DiscBotJoinListener extends ListenerAdapter {

    private Engine engine;

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
}
