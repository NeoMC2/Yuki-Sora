package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.DiscRole;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

import java.awt.*;

public class DiscCmdSetup implements DiscCommand {

    private Engine engine;

    public DiscCmdSetup(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return engine.getDiscEngine().getUtilityBase().userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel());
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "create":
                    break;

                case "add":
                    if (args.length > 2) {
                        switch (args[1]){
                            case "certchannel":
                                server.setCertificationChannelId(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces("Setted certification channel!", event.getChannel());
                                break;

                            case "role":
                                if(args.length>2){
                                    if(args[2].equals("?")){
                                        sendRoleTypes(event.getChannel());
                                        return;
                                    }
                                }
                                if (args.length >= 4) {
                                    Role role = event.getGuild().getRoleById(args[2]);
                                    if(role==null){
                                        engine.getDiscEngine().getTextUtils().sendError("Die Rolle existiert nicht!", event.getChannel(), false);
                                        return;
                                    }
                                    DiscRole discRole = new DiscRole();
                                    DiscRole.RoleType roleType = null;
                                    for (int i = 3; i < args.length; i++) {
                                        roleType = DiscRole.getRoleTypeFromString(args[i]);
                                        if(roleType == null){
                                            engine.getUtilityBase().printOutput("Invalid Role type", true);
                                        }
                                        discRole.addRoleType(roleType);
                                    }
                                    server.addRole(discRole);
                                    engine.getDiscEngine().getTextUtils().sendSucces("Added Role!", event.getChannel());
                                } else {
                                    engine.getDiscEngine().getTextUtils().sendError("Zu wenig Argumente", event.getChannel(), false);
                                }
                                break;
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError("Zu wenig Argumente", event.getChannel(), false);
                    }
                    break;
                    
                case "deinstall":
                    engine.getDiscEngine().getTextUtils().sendCustomMessage("Bist du sicher, dass du diesen Server deinstallieren möchtest? Das bedeutet, dass alle gespeicherten Dateien dieses Servers verloren gehen.\n\nZum deinstallieren `yes/y/ja/j` schreiben\nZum abbrechen `no/nein/n`", event.getChannel(), "Deinstalation", Color.blue);
                    Response deinstallResponse = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                            switch (respondingEvent.getMessage().getContentRaw()) {
                                case "y":
                                case "yes":
                                case "ja":
                                case "j":
                                    deinstallServer(event, server);
                                    break;

                                case "no":
                                case "nein":
                                case "n":
                                    engine.getDiscEngine().getTextUtils().sendWarining("Deinstallation wird abgebrochen!", respondingEvent.getChannel());
                                    break;
                            }
                        }
                    };
                    deinstallResponse.discUserId = event.getAuthor().getId();
                    deinstallResponse.discChannelId = event.getChannel().getId();
                    deinstallResponse.discGuildId = event.getGuild().getId();
                    engine.getResponseHandler().makeResponse(deinstallResponse);
                    break;

                case "start":
                    if(server.isSetupDone()){
                        engine.getDiscEngine().getTextUtils().sendCustomMessage("Es scheint so, als wäre das Setup bereits abgeschlossen!", event.getChannel(), "Setup abbruch", Color.red);
                        return;
                    }
                    engine.getDiscEngine().getTextUtils().sendCustomMessage("Bist du sicher, dass du das setup starten möchtest? Das bedeutet, dass der server neue Kategorien sowie Rollen und Channel erhält!\n\nZum starten `yes/y/ja/j` schreiben\nZum abbrechen `no/nein/n`", event.getChannel(), "Setup", Color.blue);
                    Response setupResponse = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                            switch (respondingEvent.getMessage().getContentRaw()) {
                                case "y":
                                case "yes":
                                case "ja":
                                case "j":
                                    setupServer(event, engine, server);
                                    break;

                                case "no":
                                case "nein":
                                case "n":
                                    engine.getDiscEngine().getTextUtils().sendWarining("Setup wird abgebrochen!", respondingEvent.getChannel());
                                    break;
                            }
                        }
                    };
                    setupResponse.discUserId = event.getAuthor().getId();
                    setupResponse.discChannelId = event.getChannel().getId();
                    setupResponse.discGuildId = event.getGuild().getId();
                    engine.getResponseHandler().makeResponse(setupResponse);
                    break;
            }
        }
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine) {
        return "**add**\ncertchannel <id> - adds certification channel\nrole <id> <types...> - adds role (type `-setup add role ?` to see the different types)";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void setupServer(GuildMessageReceivedEvent event, Engine engine, DiscApplicationServer server) {
        engine.getDiscEngine().getTextUtils().sendCustomMessage("Setup beginnt! Alle einstellungen die mit dem Bot an diesem Server bereits vorgenommen wurden, werden nun überschrieben! \n\nMöchtest du das Setup autmatisch oder Manuell ausführen?\nSchreibe `man` bzw `aut` um mit dem Setup zu beginnen!", event.getChannel(), "Setup", Color.MAGENTA);
        Response startResponse = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                switch (respondingEvent.getMessage().getContentRaw()) {
                    case "man":
                        engine.getDiscEngine().getTextUtils().sendCustomMessage("Als allererstes müssen zwei Rollen erstellt werden. \nEine Member Rolle, welche jeder Member auf diesem Guild besitzt, und damit Zugriff auf ihn hat. Bitte erstelle eine solche Rolle, mache einen Rechtsklick und kopiere die ID der Rolle und sende diese anschließend in diesen Channel!", respondingEvent.getChannel(), "Setup", Color.MAGENTA);

                        Response memberResponse = new Response(Response.ResponseTyp.Discord) {
                            @Override
                            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                if (respondingEvent.getGuild().getRoleById(respondingEvent.getMessage().getContentRaw()) == null) {
                                    engine.getDiscEngine().getTextUtils().sendCustomMessage("Die angegeben Rolle wurde nicht gefunden, versuche das Setup erneut zu starten!", respondingEvent.getChannel(), "Setup error", Color.RED);
                                    return;
                                }
                                DiscRole member = testRoleAndReturnDiscRole(respondingEvent.getMessage().getContentRaw(), respondingEvent.getGuild(), DiscRole.RoleType.Member);
                                if(member == null){
                                    engine.getDiscEngine().getTextUtils().sendError("Die Rolle existiert nicht!", event.getChannel(), false);
                                    return;
                                }
                                server.setDefaultMemberRoleId(member.getId());
                                engine.getDiscEngine().getTextUtils().sendCustomMessage("Sehr gut, anschließend eine Rolle, die temporäre Gamer erhalten, also falls du mal wen nur zum kurzeitigen Zocken auf diesen Server einlädst. Bitte erstelle eine solche Rolle, mache einen Rechtsklick und kopiere die ID der Rolle und sende diese anschließend in diesen Channel!", event.getChannel(), "Setup", Color.MAGENTA);

                                Response gamerResponse = new Response(ResponseTyp.Discord) {
                                    @Override
                                    public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                        if (respondingEvent.getGuild().getRoleById(respondingEvent.getMessage().getContentRaw()) == null) {
                                            engine.getDiscEngine().getTextUtils().sendCustomMessage("Die angegeben Rolle wurde nicht gefunden, versuche das Setup erneut zu starten!", respondingEvent.getChannel(), "Setup error", Color.RED);
                                            return;
                                        }
                                        DiscRole gamer = testRoleAndReturnDiscRole(respondingEvent.getMessage().getContentRaw(), respondingEvent.getGuild(), DiscRole.RoleType.TempGamer);
                                        if(gamer == null){
                                            engine.getDiscEngine().getTextUtils().sendError("Die Rolle existiert nicht!", event.getChannel(), false);
                                            return;
                                        }
                                        server.setDefaultTempGamerRoleId(gamer.getId());
                                        engine.getDiscEngine().getTextUtils().sendCustomMessage("Sehr gut, zuletzt noch einen Text Channel für die neuen Leute, die auf diesen Guild joinen. Dieser Channel wird anschließend zum Systemchannel geändert. Außerdem wird man sich dort zertifizieren müssen, indem man die \"Regel\" akzeptiert. Bitte erstelle einen solchen Channel, mache einen Rechtsklick und kopiere die ID des Channels und sende diese anschließend in diesen Channel!", event.getChannel(), "Setup", Color.MAGENTA);
                                        Response channelResponse = new Response(ResponseTyp.Discord) {
                                            @Override
                                            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                                if (respondingEvent.getGuild().getTextChannelById(respondingEvent.getMessage().getContentRaw()) == null) {
                                                    engine.getDiscEngine().getTextUtils().sendCustomMessage("Der angegebene Channel wurde nicht gefunden, versuche das Setup erneut zu starten!", respondingEvent.getChannel(), "Setup error", Color.RED);
                                                    return;
                                                }
                                                server.setCertificationChannelId(respondingEvent.getMessage().getContentRaw());
                                                engine.getDiscEngine().getTextUtils().sendCustomMessage("Sehr gut, es werden nun noch einige Konfigurationen vorgenommen und zum ende des Setups, wirst du nochmal benachrichtigt. Du kannst mithilfe des Setup Befehls jederzeit die Rollen oder Channel ändern!", event.getChannel(), "Setup", Color.MAGENTA);
                                                TextChannel certChannel = respondingEvent.getGuild().getTextChannelById(respondingEvent.getMessage().getContentRaw());
                                                respondingEvent.getGuild().getManager().setSystemChannel(certChannel);
                                                server.setCertificationMessageId(putCertMessageIntoChannel(certChannel));
                                                server.setSetupDone(true);
                                                apply(server);
                                            }
                                        };
                                        channelResponse.discGuildId = event.getGuild().getId();
                                        channelResponse.discChannelId = event.getChannel().getId();
                                        channelResponse.discUserId = event.getAuthor().getId();
                                        engine.getResponseHandler().makeResponse(channelResponse);
                                    }
                                };
                                gamerResponse.discGuildId = event.getGuild().getId();
                                gamerResponse.discChannelId = event.getChannel().getId();
                                gamerResponse.discUserId = event.getAuthor().getId();
                                engine.getResponseHandler().makeResponse(gamerResponse);
                            }
                        };
                        memberResponse.discGuildId = event.getGuild().getId();
                        memberResponse.discChannelId = event.getChannel().getId();
                        memberResponse.discUserId = event.getAuthor().getId();
                        engine.getResponseHandler().makeResponse(memberResponse);
                        break;
                    case "aut":
                        GuildController gc = respondingEvent.getGuild().getController();
                        Role member = gc.createRole().setColor(Color.green).setName("Members").setHoisted(true).complete();
                        Role gamer = gc.createRole().setColor(Color.ORANGE).setName("🎮Gamers").setHoisted(true).complete();
                        Channel channel = gc.createTextChannel("\uD83C\uDF8Awellcome").setPosition(0).complete();
                        TextChannel textChannel = respondingEvent.getGuild().getTextChannelById(channel.getId());
                        respondingEvent.getGuild().getManager().setSystemChannel(textChannel);
                        server.setCertificationMessageId(putCertMessageIntoChannel(textChannel));
                        server.setCertificationChannelId(textChannel.getId());

                        server.setDefaultMemberRoleId(member.getId());
                        server.setDefaultTempGamerRoleId(gamer.getId());

                        server.setSetupDone(true);
                        apply(server);
                        break;
                    default:
                        engine.getDiscEngine().getTextUtils().sendCustomMessage("Falsche eingabe! Versuche das Setup neuszusarten", respondingEvent.getChannel(), "Setup error", Color.red);
                        return;
                }
                engine.getDiscEngine().getTextUtils().sendSucces("Setup erfolgreich!", respondingEvent.getChannel());
            }
        };
        startResponse.discGuildId = event.getGuild().getId();
        startResponse.discChannelId = event.getChannel().getId();
        startResponse.discUserId = event.getAuthor().getId();
        engine.getResponseHandler().makeResponse(startResponse);
    }
    
    private void deinstallServer(GuildMessageReceivedEvent event, DiscApplicationServer server){
        if(!server.isSetupDone()){
            engine.getDiscEngine().getTextUtils().sendError("Du kannst den Server nicht deinstallieren, er wurde nie installiert...es werden jedoch alle files gelöscht!", event.getChannel(), false);
            engine.getDiscEngine().getFilesHandler().getServers().remove(server.getServerID());
            return;
        }
        engine.getDiscEngine().getFilesHandler().getServers().remove(server.getServerID());
        Role gamer = event.getGuild().getRoleById(server.getDefaultTempGamerRoleId());
        Role member = event.getGuild().getRoleById(server.getDefaultMemberRoleId());
        gamer.delete().complete();
        member.delete().complete();
        TextChannel cert = event.getGuild().getTextChannelById(server.getCertificationChannelId());
        cert.delete().complete();
        engine.getDiscEngine().getTextUtils().sendSucces("Deinstallation erfolgreich!", event.getChannel());
    }

    private String putCertMessageIntoChannel(TextChannel certChanel){
        final String certificationMessage = "Willkommen auf unserem Server. Gibt eigentlich auch nur eine Regel :D Benehmt euch :D wenn ihr das akzeptiert, drückt einfach den haken. Falls ihr nur hier seit um kurz mit uns zu zocken, drückt bitte den Controller Emoji. Viel spaß :D";
        Message certMessageMessage = certChanel.sendMessage(new EmbedBuilder().setColor(Color.CYAN).setDescription(certificationMessage).setTitle("Certification").build()
        ).complete();
        certChanel.addReactionById(certMessageMessage.getId(), "✅").complete();
        certChanel.addReactionById(certMessageMessage.getId(), "❌").complete();
        certChanel.addReactionById(certMessageMessage.getId(), "\uD83C\uDFAE").complete();
        return certMessageMessage.getId();
    }

    private void apply(DiscApplicationServer server){
        engine.getDiscEngine().getFilesHandler().getServers().remove(server.getServerID());
        engine.getDiscEngine().getFilesHandler().getServers().put(server.getServerID(), server);
    }

    private DiscRole testRoleAndReturnDiscRole(String id, Guild g, DiscRole.RoleType roleType) {
        Role r = g.getRoleById(id);
        if(r==null){
            return null;
        } else {
            DiscRole role = new DiscRole();
            role.setId(id);
            role.setName(r.getName());
            role.addRoleType(roleType);
            return role;
        }
    }

    private void sendRoleTypes(TextChannel textChannel){
        engine.getDiscEngine().getTextUtils().sendHelp("**Types**\n\nmod\nadmin\ngamer\nmember\ngroup[1-6]", textChannel);
    }
}