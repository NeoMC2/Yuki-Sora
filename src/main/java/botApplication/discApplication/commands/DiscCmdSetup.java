package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.DiscRole;
import botApplication.discApplication.librarys.dungeon.queue.DungeonChannelHandler;
import botApplication.discApplication.librarys.dungeon.queue.DungeonQueueHandler;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DiscCmdSetup implements DiscCommand {

    private final DiscRole.RoleType[] publicType = {DiscRole.RoleType.Admin, DiscRole.RoleType.Member, DiscRole.RoleType.Mod, DiscRole.RoleType.Group1, DiscRole.RoleType.Group2, DiscRole.RoleType.Group3, DiscRole.RoleType.Group4, DiscRole.RoleType.Group5, DiscRole.RoleType.Group6, DiscRole.RoleType.TempGamer};
    private final Engine engine;

    public DiscCmdSetup(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return engine.getDiscEngine().getUtilityBase().userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel());
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        String text = "";
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "settings":
                    if (args.length >= 2) {
                        if (args[1].equals("?")) {
                            sendRoleTypes(event.getChannel());
                            return;
                        }
                        switch (args[1].toLowerCase()) {

                            case "deafenmove":
                                server.setMoveMemberOnSDeafen(!server.isMoveMemberOnSDeafen());
                                engine.getDiscEngine().getTextUtils().sendWarining("deafen move is now: " + server.isMoveMemberOnSDeafen() + "!", event.getChannel());
                                break;

                            case "bait":
                                VoiceChannel voiceChannel = null;

                                try {
                                    voiceChannel = event.getGuild().getVoiceChannelById(args[2]);
                                } catch (Exception e){
                                }

                                if (voiceChannel == null) {
                                    engine.getDiscEngine().getTextUtils().sendError("This channel doesn't exist", event.getChannel(), false);
                                    return;
                                }

                                server.setBaitChannel(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces("Bait channel set!", event.getChannel());
                                break;

                            case "dungeon":
                                switch (args[2].toLowerCase()) {
                                    case "queue":
                                        String textChannel = args[3];
                                        TextChannel tc;
                                        if (textChannel.toLowerCase().equals("new")) {
                                            tc = event.getGuild().createTextChannel("dungeonqueue").complete();
                                        } else
                                            tc = event.getGuild().getTextChannelById(textChannel);

                                        if (tc == null) {
                                            engine.getDiscEngine().getTextUtils().sendError("Text channel not found!", event.getChannel(), false);
                                            return;
                                        }
                                        String emoji = args[4];
                                        DungeonQueueHandler qh = new DungeonQueueHandler();
                                        qh.setEmoji(emoji);
                                        Message m = tc.sendMessage("To enter a dungeon press " + emoji + " bellow and you get a own channel!").complete();
                                        m.addReaction(emoji).queue();
                                        qh.setMsgId(m.getId());
                                        server.setDungeonQueueHandler(qh);
                                        break;

                                    case "chan":
                                    case "channel":
                                        if (server.getDungeonQueueHandler() == null) {
                                            engine.getDiscEngine().getTextUtils().sendError("You don't have a queue handler message yet!", event.getChannel(), false);
                                            return;
                                        }
                                        String roleTxt = args[4];
                                        String textChannelTxt = args[3];
                                        Role r;
                                        if (roleTxt.toLowerCase().equals("new"))
                                            r = event.getGuild().createRole().setName("dungeon").setColor(Color.GRAY).complete();
                                        else
                                            r = event.getGuild().getRoleById(roleTxt);

                                        TextChannel ttc;
                                        if (textChannelTxt.toLowerCase().equals("new"))
                                            ttc = event.getGuild().createTextChannel("dungeon").complete();
                                        else
                                            ttc = event.getGuild().getTextChannelById(textChannelTxt);

                                        if (ttc == null) {
                                            engine.getDiscEngine().getTextUtils().sendError("Text channel not found!", event.getChannel(), false);
                                            return;
                                        }
                                        if (r == null) {
                                            engine.getDiscEngine().getTextUtils().sendError("Role not found!", event.getChannel(), false);
                                            return;
                                        }
                                        DungeonChannelHandler ch = new DungeonChannelHandler(ttc.getId(), r.getId());
                                        server.getDungeonQueueHandler().getChannels().add(ch);
                                        break;
                                }
                                break;

                            case "membercount":
                            case "mc":
                                server.setMemberCountCategoryId(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces("set!", event.getChannel());
                                server.updateServerStats(engine);
                                break;

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
                                } else if (args[2].toLowerCase().equals("public") || args[2].toLowerCase().equals("all")) {
                                    ArrayList<DiscRole.RoleType> pblic = new ArrayList<>();
                                    pblic.addAll(Arrays.asList(publicType));
                                    engine.getDiscEngine().addSetupRole(event.getGuild().getId(), pblic);
                                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.roleDefined", user.getLang(), null), event.getChannel());
                                    return;
                                }
                                DiscRole.RoleType roleType = null;
                                ArrayList<DiscRole.RoleType> roleTypes = new ArrayList<>();
                                for (int i = 2; i < args.length; i++) {
                                    roleType = DiscRole.getRoleTypeFromString(args[i]);
                                    if (roleType == null) {
                                        engine.getUtilityBase().printOutput(engine.lang("general.error.404role", user.getLang(), null), true);
                                        continue;
                                    }
                                    roleTypes.add(roleType);
                                }
                                engine.getDiscEngine().addSetupRole(event.getGuild().getId(), roleTypes);
                                engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.roleDefined", user.getLang(), null), event.getChannel());
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

                            case "setup":
                                if (server.isSetupMode()) {
                                    server.setSetupMode(false);
                                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.mod.setupOff", user.getLang(), null), event.getChannel());
                                } else {
                                    server.setSetupMode(true);
                                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.mod.setupOn", user.getLang(), null), event.getChannel());
                                }
                                break;

                            case "welcometext":
                            case "welcometxt":
                                for (int i = 2; i < args.length; i++) {
                                    text = text + args[i] + " ";
                                }
                                server.setWelcomeText(text);
                                engine.getDiscEngine().getTextUtils().sendSucces("set!", event.getChannel());
                                break;

                            case "welcomechan":
                            case "welcomechannel":
                                server.setWelcomeMessageChannel(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces("set!", event.getChannel());
                                break;

                            case "defaultroles":
                            case "defroles":
                            case "dr":
                            case "drole":
                                if (args[2].toLowerCase().equals("list")) {
                                    String msg = "";
                                    try {
                                        for (String rtpy : server.getDefaultRoles()) {
                                            msg = msg + rtpy + ", ";
                                        }
                                    } catch (Exception e) {
                                        engine.getUtilityBase().printOutput("Error in role list ", true);
                                        e.printStackTrace();
                                        return;
                                    }
                                    engine.getDiscEngine().getTextUtils().sendSucces("**List**\n\n" + msg, event.getChannel());
                                    return;
                                }
                                if (args[2].toLowerCase().equals("add")) {
                                    Role g = event.getGuild().getRoleById(args[3]);
                                    if (g == null) {
                                        engine.getUtilityBase().printOutput(engine.lang("general.error.404role", user.getLang(), null), true);
                                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404role", user.getLang(), null), event.getChannel(), true);
                                    } else {
                                        server.getDefaultRoles().add(g.getId());
                                        for (Member m : event.getGuild().getMembers()) {
                                            try {
                                                event.getGuild().addRoleToMember(m, g).queue();
                                            } catch (Exception e) {
                                                engine.getUtilityBase().printOutput("[Setup cmd] role cant add", true);
                                            }
                                        }
                                        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.roleDefined", user.getLang(), null), event.getChannel());
                                    }
                                } else if (args[2].toLowerCase().equals("remove")) {
                                    server.getDefaultRoles().remove(args[3]);
                                    engine.getDiscEngine().getTextUtils().sendSucces("removed!", event.getChannel());
                                }
                                break;

                            default:
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                break;
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "create":
                    break;

                case "add":
                    if (args.length > 2) {
                        switch (args[1]) {
                            case "certchannel":
                                server.setCertificationChannelId(args[2]);
                                engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.setCertChannel", user.getLang(), null), event.getChannel());
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
                                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404role", user.getLang(), null), event.getChannel(), false);
                                        return;
                                    }
                                    DiscRole discRole = new DiscRole();
                                    DiscRole.RoleType roleType = null;
                                    for (int i = 3; i < args.length; i++) {
                                        roleType = DiscRole.getRoleTypeFromString(args[i]);
                                        if (roleType == null) {
                                            engine.getUtilityBase().printOutput(engine.lang("general.error.404role", user.getLang(), null), true);
                                            continue;
                                        }
                                        discRole.addRoleType(roleType);
                                        discRole.setName(role.getName());
                                        discRole.setId(role.getId());
                                    }
                                    server.addRole(discRole);
                                    engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.roleDefined", user.getLang(), null), event.getChannel());
                                } else {
                                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
                                }
                                break;
                            default:
                                engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                                break;
                        }
                    } else {
                        engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.notEnoughArgs", user.getLang(), null), event.getChannel(), false);
                    }
                    break;

                case "deinstall":
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.mod.deinstall", user.getLang(), null), event.getChannel(), "Deinstalation", Color.blue);
                    Response deinstallResponse = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                            switch (respondingEvent.getMessage().getContentRaw()) {
                                case "y":
                                case "yes":
                                case "ja":
                                case "j":
                                    deinstallServer(event, server, user);
                                    break;

                                case "no":
                                case "nein":
                                case "n":
                                    engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("cmd.setup.info.deinstallStopped", user.getLang(), null), respondingEvent.getChannel());
                                    break;

                                default:
                                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
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
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.info.setupAlreadyDone", user.getLang(), null), event.getChannel(), "Setup abbruch", Color.red);
                        return;
                    }
                    engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.mod.install", user.getLang(), null), event.getChannel(), "Setup", Color.blue);
                    Response setupResponse = new Response(Response.ResponseTyp.Discord) {
                        @Override
                        public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                            switch (respondingEvent.getMessage().getContentRaw()) {
                                case "y":
                                case "yes":
                                case "ja":
                                case "j":
                                    setupServer(event, engine, server, user);
                                    break;

                                case "no":
                                case "nein":
                                case "n":
                                    engine.getDiscEngine().getTextUtils().sendWarining(engine.lang("setup.info.deinstallStopped", user.getLang(), null), respondingEvent.getChannel());
                                    break;

                                default:
                                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
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
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
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
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.setup.help", user.getLang(), null);
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private void setupServer(GuildMessageReceivedEvent event, Engine engine, DiscApplicationServer server, DiscApplicationUser user) {
        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.info.startSetup", user.getLang(), null), event.getChannel(), "Setup", Color.MAGENTA);
        Response startResponse = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                switch (respondingEvent.getMessage().getContentRaw()) {
                    case "man":
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.info.createRole", user.getLang(), null), respondingEvent.getChannel(), "Setup", Color.MAGENTA);

                        Response memberResponse = new Response(Response.ResponseTyp.Discord) {
                            @Override
                            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                if (respondingEvent.getGuild().getRoleById(respondingEvent.getMessage().getContentRaw()) == null) {
                                    engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("general.error.404role", user.getLang(), null), respondingEvent.getChannel(), "Setup error", Color.RED);
                                    return;
                                }
                                DiscRole member = testRoleAndReturnDiscRole(respondingEvent.getMessage().getContentRaw(), respondingEvent.getGuild(), DiscRole.RoleType.Member);
                                if (member == null) {
                                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404role", user.getLang(), null), event.getChannel(), false);
                                    return;
                                }
                                server.setDefaultMemberRoleId(member.getId());
                                engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.info.createGamer", user.getLang(), null), event.getChannel(), "Setup", Color.MAGENTA);

                                Response gamerResponse = new Response(ResponseTyp.Discord) {
                                    @Override
                                    public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                        if (respondingEvent.getGuild().getRoleById(respondingEvent.getMessage().getContentRaw()) == null) {
                                            engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("general.error.404role", user.getLang(), null), respondingEvent.getChannel(), "Setup error", Color.RED);
                                            return;
                                        }
                                        DiscRole gamer = testRoleAndReturnDiscRole(respondingEvent.getMessage().getContentRaw(), respondingEvent.getGuild(), DiscRole.RoleType.TempGamer);
                                        if (gamer == null) {
                                            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404role", user.getLang(), null), event.getChannel(), false);
                                            return;
                                        }
                                        server.setDefaultTempGamerRoleId(gamer.getId());
                                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.info.createCertChannel", user.getLang(), null), event.getChannel(), "Setup", Color.MAGENTA);
                                        Response channelResponse = new Response(ResponseTyp.Discord) {
                                            @Override
                                            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                                if (respondingEvent.getGuild().getTextChannelById(respondingEvent.getMessage().getContentRaw()) == null) {
                                                    engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("general.error.404channel", user.getLang(), null), respondingEvent.getChannel(), "Setup error", Color.RED);
                                                    return;
                                                }
                                                server.setCertificationChannelId(respondingEvent.getMessage().getContentRaw());
                                                engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("general.error.404channel", user.getLang(), null), event.getChannel(), "Setup", Color.MAGENTA);
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
                        Guild gc = respondingEvent.getGuild();
                        Role member = gc.createRole().setColor(Color.green).setName("Members").setHoisted(true).complete();
                        Role gamer = gc.createRole().setColor(Color.ORANGE).setName("🎮Gamers").setHoisted(true).complete();
                        TextChannel channel = gc.createTextChannel("\uD83C\uDF8Awelcome").setPosition(0).complete();
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
                        engine.getDiscEngine().getTextUtils().sendCustomMessage(engine.lang("cmd.setup.error.setupWrongArgs", user.getLang(), null), respondingEvent.getChannel(), "Setup error", Color.red);
                        return;
                }
                engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.info.autoSetupDone", user.getLang(), null), respondingEvent.getChannel());
            }
        };
        startResponse.discGuildId = event.getGuild().getId();
        startResponse.discChannelId = event.getChannel().getId();
        startResponse.discUserId = event.getAuthor().getId();
        engine.getResponseHandler().makeResponse(startResponse);
    }

    private void deinstallServer(GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user) {
        if (!server.isSetupDone()) {
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("cmd.setup.error.setupNeverStarted", user.getLang(), null), event.getChannel(), false);
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
        engine.getDiscEngine().getTextUtils().sendSucces(engine.lang("cmd.setup.succes.deinstallDone", user.getLang(), null), event.getChannel());
    }

    private String putCertMessageIntoChannel(TextChannel certChanel, DiscApplicationServer server) {
        Message certMessageMessage = certChanel.sendMessage(new EmbedBuilder().setColor(Color.CYAN).setDescription(server.getWelcomeText()).setTitle("Certification").build()
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