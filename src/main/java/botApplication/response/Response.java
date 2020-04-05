package botApplication.response;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public abstract class Response {

    public Response(ResponseTyp responseTyp) {
        this.responseTyp = responseTyp;
    }

    public User teleResponseUser;
    public Chat teleResponseChat;
    public void respondTele(Update respondingUpdate){}
    public void respondDisc(GuildMessageReceivedEvent respondingEvent){}
    public int creationTime = 0;
    public String discGuildId;
    public String discChannelId;
    public String discUserId;
    public ResponseTyp responseTyp;

    public enum ResponseTyp {
        Telegram, Discord
    }
}
