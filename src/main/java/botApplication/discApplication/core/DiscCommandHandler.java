package botApplication.discApplication.core;

import botApplication.discApplication.commands.DiscCommand;
import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscCommandHandler {

    public HashMap<String, DiscCommand> commands = new HashMap<>();
    public ArrayList<String> commandIvokes = new ArrayList<>();
    private final Engine engine;

    public DiscCommandHandler(Engine engine) {
        this.engine = engine;
    }

    public void handleServerCommand(DiscCommandParser.ServerCommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            boolean exe = commands.get(cmd.invoke).calledServer(cmd.args, cmd.event, cmd.server, cmd.user, cmd.engine);

            if (exe) {
                String args0 = "";
                try {
                    args0 = cmd.args[0];
                } catch (Exception ignored) {
                }
                if (args0.equalsIgnoreCase("help")) {
                    cmd.engine.getDiscEngine().getTextUtils().sendHelp(commands.get(cmd.invoke).help(cmd.engine, cmd.user), cmd.event.getChannel());
                } else {
                    commands.get(cmd.invoke).actionServer(cmd.args, cmd.event, cmd.server, cmd.user, cmd.engine);
                }
            }
        }
    }

    public void handlePrivateCommand(DiscCommandParser.ClientCommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            boolean exe = commands.get(cmd.invoke).calledPrivate(cmd.args, cmd.event, cmd.user, cmd.engine);

            if (exe) {
                String args0 = "";
                try {
                    args0 = cmd.args[0];
                } catch (Exception ignored) {
                }

                if (args0.equalsIgnoreCase("help")) {
                    cmd.engine.getDiscEngine().getTextUtils().sendHelp(commands.get(cmd.invoke).help(cmd.engine, cmd.user), cmd.event.getChannel());
                } else {
                    commands.get(cmd.invoke).actionPrivate(cmd.args, cmd.event, cmd.user, cmd.engine);
                }
            }
        }
    }

    public void handleSlashCommand(String invoke, String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (commands.containsKey(invoke)) {

            boolean exe = commands.get(invoke).calledSlash(args, event, server, user, engine);

            if (exe) {
                String args0 = "";
                try {
                    args0 = args[0];
                } catch (Exception ignored) {
                }

                if (args0.equalsIgnoreCase("help")) {
                    engine.getDiscEngine().getTextUtils().sendHelp(commands.get(invoke).help(engine, user), event.getChannel());
                } else {
                    try {
                        commands.get(invoke).actionSlash(args, event, server, user, engine);
                    } catch (Exception e){
                        if(engine.getProperties().debug)
                            e.printStackTrace();
                        event.replyEmbeds(new EmbedBuilder().setColor(Color.RED).setAuthor("Error").setDescription("An error occurred while performing this command").build());
                    }
                }
            }
        } else{
            event.deferReply().setContent("This command wasn't found").queue();
        }
    }

    public void createNewCommand(DiscCommand cmd) {
        if (commandIvokes.contains(cmd.getInvoke())) {
            engine.getUtilityBase().printOutput("Command " + cmd.getInvoke() + " already exist!", true);
        } else {
            commands.put(cmd.getInvoke(), cmd);
            commandIvokes.add(cmd.getInvoke());
            engine.getUtilityBase().printOutput( "Command " + cmd.getInvoke() + " added!", true);
        }
    }

    public void registerSlashCommands(){
        ArrayList<CommandData> list = new ArrayList<>();
        for (String s: commandIvokes) {
            DiscCommand cmd = commands.get(s);
            if(cmd.getCommand() != null)
            list.add(cmd.getCommand());
        }
        engine.getDiscEngine().getBotJDA().updateCommands().addCommands(list).queue();
    }
}
