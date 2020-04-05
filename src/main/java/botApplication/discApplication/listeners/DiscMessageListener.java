package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscMessageListener extends ListenerAdapter {

    private Engine engine;

    public DiscMessageListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if(event.getMessage().getContentRaw().length()>400){
            return;
        }

        Member selfUser = event.getGuild().getMemberById(event.getGuild().getJDA().getSelfUser().getId());
        boolean commandWorked = false;

        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            if(engine.getResponseHandler().lookForResponse(event)){
                return;
            }
            handleSecret(event);
                    if (event.getMessage().getContentRaw().startsWith(engine.getProperties().discBotApplicationPrefix)) {
                        boolean hasPermission = false;
                        try {
                            hasPermission = selfUser.hasPermission(Permission.ADMINISTRATOR);
                        } catch (Exception e) {
                            engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + " Bot has no permissions",true);
                        }

                        if (hasPermission) {
                            //command exist check
                            for (int i = 0; engine.getDiscEngine().getCommandHandler().commandIvokes.size() > i; i++) {
                                if (event.getMessage().getContentRaw().contains(engine.getDiscEngine().getCommandHandler().commandIvokes.get(i))) {
                                    sendGuildCommand(event);
                                    //event.getMessage().delete().queue();
                                    commandWorked = true;
                                    break;
                                }
                            }
                            if (!commandWorked) {
                                engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + "command " + event.getMessage().getContentRaw() + " doesnt exist!",true);
                                //engine.getDiscEngine().getTextUtils().deletUserMessage(1, event);
                                engine.getDiscEngine().getTextUtils().sendError("DicCommand " + event.getMessage().getContentRaw() + "  existiert nicht!\n\nSchreibe **" + engine.getProperties().discBotApplicationPrefix + "help** um eine auflistung der Commands zu erhalten.", event.getChannel(), engine.getProperties().middleTime, true);
                            }
                        } else {
                            engine.getUtilityBase().printOutput(messageInfo(event.getGuild()) + " bot has not the permission!",true);
                            engine.getDiscEngine().getTextUtils().sendError("Der bot hat nicht die nötigen berechtigungen!\n\nBitte weise ihm die Admin rechte zu!", event.getChannel(), engine.getProperties().longTime, true);
                        }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().length()>400){
            return;
        }
        boolean commandWorked = false;

        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            engine.getUtilityBase().printOutput(" message listener received guild message!",true);
            if (event.getMessage().getContentRaw().startsWith(engine.getProperties().discBotApplicationPrefix)) {
                    //command exist check
                    for (int i = 0; engine.getDiscEngine().getCommandHandler().commandIvokes.size() > i; i++) {
                        if (event.getMessage().getContentRaw().contains(engine.getDiscEngine().getCommandHandler().commandIvokes.get(i))) {
                            sendPrivateCommand(event);
                            commandWorked = true;
                            break;
                        }
                    }
                    if (!commandWorked) {
                        engine.getDiscEngine().getTextUtils().sendError("DicCommand " + event.getMessage().getContentRaw() + "  existiert nicht!\n\nSchreibe **" + engine.getProperties().discBotApplicationPrefix + "help** um eine auflistung der Commands zu erhalten.", event.getChannel(), engine.getProperties().middleTime, true);
                    }
            }
        }
    }

    private String messageInfo(Guild guild) {
        return "[serverMessageListener -" + guild.getName() + "|" + guild.getId() + "]";
    }

    private void handleSecret(GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.length() > 3) {
            String[] msgArgs = message.split(" ");

            //contains
            for (int i = 0; msgArgs.length > i; i++) {
                switch (msgArgs[i].toLowerCase()) {
                    case "lol":
                    case "lool":
                        sendNormalText(event, "Ja loool eyyy was ist lol? XD");
                        return;
                    case "anime":
                        sendNormalText(event, "Who said Anime :O");
                        return;
                    case "baka":
                        sendNormalText(event, "Baaaaaaaaaaaaaka! XD");
                        return;
                    case "jaja":
                        sendNormalText(event, "JAJA HEIßT LECK MICH AM ARSCH :D ");
                        return;
                    case "aloha":
                        sendNormalText(event, "Aloha " + event.getAuthor().getName() + " XD \nDas hat mir der Mosel beigebracht ");
                        return;
                }
            }
        }
        return;

    }

    private void sendNormalText(GuildMessageReceivedEvent event, String Message) {
        engine.getDiscEngine().getTextUtils().sendNormalTxt(Message, event.getChannel());
    }

    private void sendGuildCommand(GuildMessageReceivedEvent event){
        DiscApplicationUser user = engine.getDiscEngine().getUtilityBase().lookForUserById(event.getAuthor());
        DiscApplicationServer server = engine.getDiscEngine().getUtilityBase().lookForServer(event.getGuild());
        try {
            engine.getDiscEngine().getCommandHandler().handleServerCommand(engine.getDiscEngine().getCommandParser().parseServerMessage(event.getMessage().getContentRaw(), event, server, user, engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----",true);
            e.printStackTrace();
        }
    }

    private void sendPrivateCommand(PrivateMessageReceivedEvent event){
        DiscApplicationUser user = engine.getDiscEngine().getUtilityBase().lookForUserById(event.getAuthor());

        try {
            engine.getDiscEngine().getCommandHandler().handlePrivateCommand(engine.getDiscEngine().getCommandParser().parseClientMessage(event.getMessage().getContentRaw(),event, user,engine));
        } catch (Exception e) {
            engine.getDiscEngine().getTextUtils().sendError("Fatal command error on command: " + event.getMessage().getContentRaw(), event.getChannel(), true);
            engine.getUtilityBase().printOutput("-----\n[Send server command failed]\n-----",true);
            e.printStackTrace();
        }
    }
}
