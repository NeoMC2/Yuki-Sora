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
import java.util.ArrayList;
import java.util.Arrays;

public class DiscCmdSetup implements DiscCommand {

    private Engine engine;

    private final DiscRole.RoleType[] publicType = {DiscRole.RoleType.Admin, DiscRole.RoleType.Member, DiscRole.RoleType.Mod, DiscRole.RoleType.Group1, DiscRole.RoleType.Group2, DiscRole.RoleType.Group3, DiscRole.RoleType.Group4, DiscRole.RoleType.Group5, DiscRole.RoleType.Group6, DiscRole.RoleType.TempGamer};

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
                case "settings":
                    if (args.length >= 2) {
                        if (args[1].equals("?")) {
                            sendRoleTypes(event.getChannel());
                            return;
                        }
                        switch (args[1].toLowerCase()) {
                            case "channelrole":
                            case "chrole":
                            case "chanrole":
                            case "chr":
                            case "cr":
                                if (args[2].toLowerCase().equals("list")) {
                                    String msg = "";
                                    try {
                                        for (DiscRole.RoleType rtpy : engine.getDiscEngine().getSetupRoles().get(event.getGuild().getId())) {
                                            msg = msg + rtpy.toString() + ", ";
                                        }
                                    } catch (Exception e) {
                                        engine.getUtilityBase().printOutput("Error in chanrole list ", true);
                                        e.printStackTrace();
                                        return;
                                    }
                                    engine.getDiscEngine().getTextUtils().sendSucces("**List**\n\n" + msg, event.getChannel());
                                    return;
                                } else if(args[2].toLowerCase().equals("public")||args[2].toLowerCase().equals("all")){
                                    ArrayList<DiscRole.RoleType> pblic = new ArrayList<>();
                                    pblic.addAll(Arrays.asList(publicType));
                                    engine.getDiscEngine().addSetupRole(event.getGuild().getId(), pblic);
                                    engine.getDiscEngine().getTextUtils().sendSucces("New Roles defined!", event.getChannel());
                                    return;
                                }
                                DiscRole.RoleType roleType = null;
                                ArrayList<DiscRole.RoleType> roleTypes = new ArrayList<>();
                                for (int i = 2; i < args.length; i++) {
                                    roleType = DiscRole.getRoleTypeFromString(args[i]);
                                    if (roleType == null) {
                                        engine.getUtilityBase().printOutput("Invalid Role type", true);
                                        continue;
                                    }
                                    roleTypes.add(roleType);
                                }
                                engine.getDiscEngine().addSetupRole(event.getGuild().getId(), roleTypes);
                                engine.getDiscEngine().getTextUtils().sendSucces("New Roles defined!", event.getChannel());
                                break;

                            case "ruletxt":
                            case "rule":
                            case "rules":
                                String text = "";
                                for (int i = 2; i < args.length; i++) {
                                    text = text + args[i] + " ";
                                }
                                server.setRuleText(text);
                                engine.getDiscEngine().getTextUtils().sendSucces("New rule text defined!", event.getChannel());
                                break;

                            case "renewcertificationchannel":
                            case "renewcert":
                            case "certrenew":
                            case "recert":
                            case "certre":
                                TextChannel certChannel = event.getGuild().getTextChannelById(server.getCertificationChannelId());
                                try {
                                    certChannel.getHistory().getRetrievedHistory().forEach(message -> message.delete().complete());
                                } catch (Exception e) {
                                }
                                server.setCertificationMessageId(putCertMessageIntoChannel(certChannel, server));
                                break;
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError("Zu wenig Argumente", event.getChannel(), false);
                    }
                    break;

                case "create":
                    break;

                case "add":
                    if (args.length > 2) {
                        switch (args[1]) {
                            case "certchannel":
                                server.setCertificationChannelId(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces("Setted certification channel!", event.getChannel());
                                break;

                            case "role":
                                if (args.length > 2) {
                                    if (args[2].equals("?")) {
                                        sendRoleTypes(event.getChannel());
                                        return;
                                    }
                                }
                                if (args.length >= 4) {
                                    Role role = event.getGuild().getRoleById(args[2]);
                                    if (role == null) {
                                        engine.getDiscEngine().getTextUtils().sendError("Die Rolle existiert nicht!", event.getChannel(), false);
                                        return;
                                    }
                                    DiscRole discRole = new DiscRole();
                                    DiscRole.RoleType roleType = null;
                                    for (int i = 3; i < args.length; i++) {
                                        roleType = DiscRole.getRoleTypeFromString(args[i]);
                                        if (roleType == null) {
                                            engine.getUtilityBase().printOutput("Invalid Role type", true);
                                            continue;
                                        }
                                        discRole.addRoleType(roleType);
                                        discRole.setName(role.getName());
                                        discRole.setId(role.getId());
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
                    if (server.isSetupDone()) {
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

                default:
                    engine.getDiscEngine().getTextUtils().sendError("Unknown command arguments!", event.getChannel(), false);
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
        return "start - starts install setup\ndeinstall - starts deinstall setup\n\n**add**\ncertchannel <id> - adds certification channel\nrole <id> <types...> - adds role (type `-setup add role ?` to see the different types)\n\n**settings**\nchannelrole <types.../all> - changes the role for channels you are going to create\nruletxt <text...> - changes the ruletext in the welcome channel\nrenewCertificationChannel - deletes old message and prints new one";
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
                                if (member == null) {
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
                                        if (gamer == null) {
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
                                                server.setCertificationMessageId(putCertMessageIntoChannel(certChannel, server));
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
                        Channel channel = gc.createTextChannel("\uD83C\uDF8Awelcome").setPosition(0).complete();
                        TextChannel textChannel = respondingEvent.getGuild().getTextChannelById(channel.getId());
                        respondingEvent.getGuild().getManager().setSystemChannel(textChannel);

                        server.addRole(testRoleAndReturnDiscRole(member.getId(), respondingEvent.getGuild(), DiscRole.RoleType.Member));
                        server.addRole(testRoleAndReturnDiscRole(gamer.getId(), event.getGuild(), DiscRole.RoleType.TempGamer));

                        server.setCertificationMessageId(putCertMessageIntoChannel(textChannel, server));
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

    private void deinstallServer(GuildMessageReceivedEvent event, DiscApplicationServer server) {
        if (!server.isSetupDone()) {
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

    private String putCertMessageIntoChannel(TextChannel certChanel, DiscApplicationServer server) {
        final String certificationMessage = "Bist du nur hier um kurz ein paar Runden zu zocken? Dann drück einfach \uD83C\uDFAE\n\nWenn du dem Server beitreten willst, drück einfach ✅\n\nAnsonsten ❌, das ändert aber nix loolz";
        if (!server.getRuleText().equals("")) {
            Message certMessageMessage = certChanel.sendMessage(new EmbedBuilder().setColor(Color.YELLOW).setDescription(server.getRuleText()).setTitle("Rules").build()
            ).complete();
        }
        Message certMessageMessage = certChanel.sendMessage(new EmbedBuilder().setColor(Color.CYAN).setDescription(certificationMessage).setTitle("Certification").build()
        ).complete();
        certChanel.addReactionById(certMessageMessage.getId(), "✅").complete();
        certChanel.addReactionById(certMessageMessage.getId(), "❌").complete();
        certChanel.addReactionById(certMessageMessage.getId(), "\uD83C\uDFAE").complete();
        return certMessageMessage.getId();
    }

    private void apply(DiscApplicationServer server) {
        engine.getDiscEngine().getFilesHandler().getServers().remove(server.getServerID());
        engine.getDiscEngine().getFilesHandler().getServers().put(server.getServerID(), server);
    }

    private DiscRole testRoleAndReturnDiscRole(String id, Guild g, DiscRole.RoleType roleType) {
        Role r = g.getRoleById(id);
        if (r == null) {
            return null;
        } else {
            DiscRole role = new DiscRole();
            role.setId(id);
            role.setName(r.getName());
            role.addRoleType(roleType);
            return role;
        }
    }

    private void sendRoleTypes(TextChannel textChannel) {
        engine.getDiscEngine().getTextUtils().sendHelp("**Types**\n\nmod\nadmin\ngamer\nmember\ngroup[1-6]", textChannel);
    }
}