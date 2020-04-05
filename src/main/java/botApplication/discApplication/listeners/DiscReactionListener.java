package botApplication.discApplication.listeners;

import core.Engine;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class DiscReactionListener extends ListenerAdapter {

    private Engine engine;

    public DiscReactionListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if(!exec(event.getReaction().getUsers().complete(), event.getMember().getUser().getId()))
            return;

        switch (event.getReactionEmote().getName()) {
            //haken-Emoji
            case "✅":
                engine.getDiscEngine().getCertificationHandler().addMemberCertification(event.getMember(), event.getGuild());
                break;

            //X-Emoji
            case "❌":
                engine.getDiscEngine().getCertificationHandler().removeCertification(event.getMember(), event.getGuild());
                break;

            //Game-Emoji
            case "\uD83C\uDFAE":
                engine.getDiscEngine().getCertificationHandler().addTempGameCertification(event.getMember(), event.getGuild());
                break;
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        switch (event.getReactionEmote().getName()) {

            //X-Emoji
            case "❌":
                break;

            //haken-Emoji
            //Game-Emoji
            case "✅":
            case "\uD83C\uDFAE":
                engine.getDiscEngine().getCertificationHandler().removeCertification(event.getMember(), event.getGuild());
                break;
        }
    }

    @Override
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event) {

    }

    private boolean exec(List<User> users, String userId){
        int found = 0;
        for (User lUser:users) {
            if(lUser.getId().equals(userId))
                found++;
        }
        return found<2;
    }
}
