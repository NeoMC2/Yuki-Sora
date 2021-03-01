package botApplication.response;

/*
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
 */

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Date;

public abstract class Response {

    //public User teleResponseUser;
    //public Chat teleResponseChat;
    public Date creationTime = new Date();
    public String discGuildId;
    public String discChannelId;
    public String discUserId;
    public ResponseTyp responseTyp;

    public Response(ResponseTyp responseTyp) {
        this.responseTyp = responseTyp;
    }

    /*
    public void respondTele(Update respondingUpdate) {
    }
     */

    public void onEmote(GuildMessageReactionAddEvent respondingEvent) {
    }

    public void onMessage(GuildMessageReceivedEvent respondingEvent) {
    }

    public void onError(Exception e){

    }

    public enum ResponseTyp {
        Discord, DiscordReact
    }
}
