package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.poll.Poll;
import botApplication.discApplication.librarys.poll.PollAnswer;
import core.Engine;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class DiscCmdVote implements DiscCommand {

    private final Engine engine;
    public ArrayList<Poll> polls;
    String[] counting = {"0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "\uD83D\uDD1F"};

    public DiscCmdVote(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        } else {
            engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.noadmin", user.getLang(), null), event.getChannel(), engine.getProperties().middleTime, false);
            return false;
        }
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length > 1)
            switch (args[0]) {
                case "create":
                    if (args[1].equals("lang")) {
                        ArrayList<String> langs = new ArrayList<String>();
                        for (int i = 3; i < args.length; i++) {
                            langs.add(args[i]);
                        }
                        TextChannel tchan = event.getGuild().getTextChannelById(args[2]);
                        if (tchan == null) {
                            return;
                        } else {
                            Poll poll = new Poll(event.getGuild().getId());
                            poll.setChannel(tchan.getId());
                            poll.setCreator(event.getAuthor().getId());
                            poll.setHeading("Select your language");
                            poll.setPollType(PollType.Lang);
                            int num = 1;

                            for (String s : langs) {
                                PollAnswer pa = new PollAnswer();
                                pa.setAnswer(s);
                                pa.setAnswerEmoji(counting[num]);
                                num++;
                                pa.setLang(s);
                                poll.addAnswer(pa);
                            }


                            poll.create(event.getGuild(), engine, event.getChannel());
                            polls.add(poll);
                            engine.getDiscEngine().getTextUtils().sendSucces("New vote created!", event.getChannel());
                        }
                        return;
                    }
                    int ansCounter = 1;
                    String unFString = event.getMessage().getContentDisplay();
                    String[] cmArgs = unFString.split("§");
                    Poll poll = new Poll(event.getGuild().getId());
                    poll.setCreator(event.getMember().getUser().getId());
                    for (String cmArg : cmArgs) {
                        cmArg.replace("\"", "");
                        cmArg.replace(">", "");
                        String spec = "";
                        String specCont = "";
                        try {
                            for (int i = 0; i < cmArg.length(); i++) {
                                if (cmArg.charAt(i) == '=') {
                                    spec = cmArg.substring(0, i);
                                    specCont = cmArg.substring(i + 1);
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }

                        switch (spec) {
                            case "color":
                                Color c = Color.cyan;
                                try {
                                    c = Color.decode(specCont);
                                } catch (Exception e) {
                                    engine.getDiscEngine().getTextUtils().sendError("Formatting error, color is invalid! (Use hex color)", event.getChannel(), true);
                                }
                                poll.setColor(c);
                                break;

                            case "answers":
                                PollAnswer pollAnswer = null;
                                String aSpec = "";
                                String aCont = "";
                                String[] cmArgss = specCont.split("<");
                                for (String tt : cmArgss) {
                                    if (tt.length() < 4)
                                        continue;
                                    pollAnswer = new PollAnswer();
                                    String[] ttT = tt.split("%");
                                    for (String ttt : ttT) {
                                        if (ttt.length() < 4)
                                            continue;
                                        pollAnswer.setPlace(ansCounter);
                                        ansCounter++;

                                        for (int i = 0; i < ttt.length(); i++) {
                                            if (ttt.charAt(i) == '=') {
                                                aSpec = ttt.substring(0, i);
                                                aCont = ttt.substring(i + 1);
                                                break;
                                            }
                                        }


                                        switch (aSpec) {
                                            case "emoji":
                                                pollAnswer.setAnswerEmoji(aCont);
                                                try{
                                                    if(event.getGuild().getEmotesByName(pollAnswer.getAnswerEmoji().replace(":", ""), false).size() > 0){
                                                        pollAnswer.setEmojiServerEmote(true);
                                                        pollAnswer.setAnswerEmoji(pollAnswer.getAnswerEmoji().replace(":", ""));
                                                    }
                                                } catch (Exception e){
                                                }
                                                break;
                                            case "answer":
                                                pollAnswer.setAnswer(aCont);
                                                break;
                                            case "role":
                                                poll.setPollType(PollType.UserProperty);
                                                pollAnswer.setRole(aCont);
                                                break;
                                        }

                                    }
                                    poll.addAnswer(pollAnswer);
                                }
                                break;

                            case "topic":
                            case "heading":
                                poll.setHeading(specCont);
                                break;

                            case "channel":
                                try {
                                    poll.setChannel(event.getGuild().getTextChannelById(specCont).getId());
                                } catch (Exception e) {
                                    engine.getDiscEngine().getTextUtils().sendError("Formatting error, channel is invalid!", event.getChannel(), true);
                                }
                                break;
                        }
                    }
                    if(!poll.create(event.getGuild(), engine, event.getChannel()))
                        return;
                    polls.add(poll);
                    engine.getDiscEngine().getTextUtils().sendSucces("New vote created!", event.getChannel());
                    break;

                case "remove":
                    for (Poll p : polls) {
                        if (args[1].equals(p.getMessageId())) {
                            polls.remove(p);
                            event.getGuild().getTextChannelById(p.getChannel()).deleteMessageById(p.getMessageId()).queue();
                            engine.getDiscEngine().getTextUtils().sendSucces("Removed!", event.getChannel());
                            return;
                        }
                    }
                    break;

                default:
                    engine.getDiscEngine().getTextUtils().sendError(engine.lang("general.error.404cmdArg", user.getLang(), null), event.getChannel(), false);
                    break;
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
    public boolean calledSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return false;
    }

    @Override
    public void actionSlash(String[] args, SlashCommandEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {

    }

    @Override
    public String help(Engine engine, DiscApplicationUser user) {
        return engine.lang("cmd.vote.help", user.getLang(), null);
    }

    @Override
    public CommandData getCommand() {
        return null;
    }

    @Override
    public String getInvoke() {
        return "vote";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    public ArrayList<Poll> getPolls() {
        return polls;
    }

    public void setPolls(ArrayList<Poll> polls) {
        this.polls = polls;
    }

    public enum PollType implements Serializable {
        Vote, UserProperty, Lang
    }
}
