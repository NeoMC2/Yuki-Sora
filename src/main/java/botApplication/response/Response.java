package botApplication.response;

/*
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
 */

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class Response {

    //public User teleResponseUser;
    //public Chat teleResponseChat;
    public int creationTime = 0;
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

    public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
    }

    public void onError(Exception e){

    }

    public enum ResponseTyp {
        Discord
    }
}
