package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import botApplication.response.Response;
import core.Engine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.io.Serializable;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscCmdContest implements DiscCommand {

    private ArrayList<Contest> contests;

    public DiscCmdContest(Engine engine) {
        try {
            contests = (ArrayList<Contest>) engine.getFileUtils().loadObject(engine.getFileUtils().home + "/contest.dat");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("Faild loading contest data!", true);
            contests = new ArrayList<>();
        }
    }

    public void saveContests(Engine engine) {
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().home + "/contest.dat", contests);
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("Faild saving contest data!", true);
        }
    }

    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return DiscUtilityBase.userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel(), engine);
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "create":
                    switch (args[1].toLowerCase()) {
                        case "art":
                            engine.getDiscEngine().getTextUtils().sendWarining("Type in the id of the Category the channel will be created in", event.getChannel());
                            Response r = new Response(Response.ResponseTyp.Discord) {
                                @Override
                                public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                                    Category category = event.getGuild().getCategoryById(respondingEvent.getMessage().getContentRaw());
                                    if (category == null) {
                                        engine.getDiscEngine().getTextUtils().sendError("This category is invalid!", event.getChannel(), false);
                                        return;
                                    }
                                    engine.getDiscEngine().getTextUtils().sendWarining("When will the contest be over? Type the Date like:\n`dd.MM.yyyy HH:mm`", event.getChannel());

                                    Response rr = new Response(Response.ResponseTyp.Discord) {
                                        @Override
                                        public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                                            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                            Date date = null;
                                            try {
                                                date = dateFormat.parse(respondingEvent.getMessage().getContentRaw());
                                            } catch (ParseException e) {
                                                engine.getDiscEngine().getTextUtils().sendError("This date is invalid!", event.getChannel(), false);
                                                return;
                                            }

                                            engine.getDiscEngine().getTextUtils().sendWarining("How many weboos will the winner get?", event.getChannel());
                                            Date finalDate = date;

                                            Response rrr = new Response(ResponseTyp.Discord) {
                                                @Override
                                                public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                                                    int reward = 0;
                                                    try {
                                                        reward = Integer.parseInt(respondingEvent.getMessage().getContentRaw());
                                                    } catch (Exception e) {
                                                        engine.getDiscEngine().getTextUtils().sendError("This Integer is invalid!", event.getChannel(), false);
                                                        return;
                                                    }

                                                    engine.getDiscEngine().getTextUtils().sendWarining("Whats the topic of this art contest?", event.getChannel());
                                                    int finalReward = reward;
                                                    Response rrrr = new Response(ResponseTyp.Discord) {
                                                        @Override
                                                        public void onGuildMessage(GuildMessageReceivedEvent respondingEvent) {
                                                            String topic = respondingEvent.getMessage().getContentRaw();
                                                            TextChannel tc = createContest(category, "art-contest", respondingEvent.getGuild().getRoleById(server.getDefaultMemberRoleId()));
                                                            Contest contest = new Contest(tc, finalReward, finalDate);
                                                            contests.add(contest);

                                                            Message mb = new MessageBuilder().setContent("Hey <@&" + server.getDefaultMemberRoleId() + "> we are starting a art contest here! If you want to be a part of this contest upload your file to participate!\n\n" +
                                                                    "The topic of this contest is: `" + topic + "`\n\nThe winner gets `" + finalReward + " weboos` as reward\n\n" +
                                                                    "Allowed are all kinds of art which match the topic\n• Drawings\n• Modeling\n• Music etc.\n\nForbidden are all kinds of NSFW or violent content.\n\nYou can only upload one file to participate!\n" +
                                                                    "Everyone can vote for the best art peace by pressing the :white_check_mark: after the upload phase is over!\n\n!You can only vote once! A second vote will be deleted until you removed your first vote!\n\n" +
                                                                    "Have fun! :confetti_ball:").build();
                                                            tc.sendMessage(mb).queue();
                                                            engine.getDiscEngine().getTextUtils().sendSucces("The contest is created!", event.getChannel());
                                                        }
                                                    };
                                                    rrrr.discUserId = event.getAuthor().getId();
                                                    rrrr.discChannelId = event.getChannel().getId();
                                                    engine.getResponseHandler().makeResponse(rrrr);
                                                }
                                            };
                                            rrr.discUserId = event.getAuthor().getId();
                                            rrr.discChannelId = event.getChannel().getId();
                                            engine.getResponseHandler().makeResponse(rrr);

                                        }
                                    };
                                    rr.discUserId = event.getAuthor().getId();
                                    rr.discChannelId = event.getChannel().getId();
                                    engine.getResponseHandler().makeResponse(rr);
                                }
                            };
                            r.discUserId = event.getAuthor().getId();
                            r.discChannelId = event.getChannel().getId();
                            engine.getResponseHandler().makeResponse(r);
                            break;
                    }
                    break;

                case "close":
                    Contest contest = checkChannel(engine, event.getChannel());
                    close(contest, engine, event.getChannel());
                    break;
            }
        }
    }

    private void close(Contest contest, Engine engine, TextChannel tc) {
        if (contest.isOpen) {
            contest.setOpen(false);
            engine.getDiscEngine().getTextUtils().sendError("The submission of your works has now ended! You can vote for the best now!", tc, false);
        } else {
            engine.getDiscEngine().getTextUtils().sendError("The voting has ended now!", tc, false);
            contests.remove(contest);

            int highest = 0;
            ArrayList<Message> messages = new ArrayList<>();
            for (Message msg : tc.getHistory().retrievePast(99).complete()) {
                if (msg.getReactions().size() > 0) {
                    if (msg.getReactions().get(0).getCount() > highest) {
                        messages.clear();
                        messages.add(msg);
                        highest = msg.getReactions().get(0).getCount();
                    } else if (msg.getReactions().get(0).getCount() == highest) {
                        messages.add(msg);
                    }
                }
            }

            String winner = "";
            if (messages.size() > 1) {
                winner = "The winners are ";
                for (Message msg : messages) {
                    winner += msg.getAuthor().getName() + " ";
                }
                winner += "\nThey receive a reward of " + contest.getReward() + " weboos! Congratulations :confetti_ball:";
            } else {
                winner = "The winner is " + messages.get(0).getAuthor().getName();
                winner += "\nThe winner gets " + contest.getReward() + " weboos! Congratulations :confetti_ball:";
            }

            engine.getDiscEngine().getTextUtils().sendSucces(winner, tc);

            for (Message msg : messages) {
                engine.getDiscEngine().getApiManager().giveCoinsToUser(msg.getAuthor().getId(), contest.getReward());
            }

            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    tc.delete().queue();
                }
            };

            Timer t = new Timer();
            t.schedule(tt, 10 * 10 * 60 * 5);
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
        return null;
    }

    @Override
    public void actionTelegram(Member member, Engine engine, DiscApplicationUser user, String[] args) {

    }

    private TextChannel createContest(Category category, String channelName, Role role) {
        TextChannel tc = category.createTextChannel(channelName).syncPermissionOverrides().complete();
        tc.createPermissionOverride(role).setAllow(Permission.MESSAGE_ATTACH_FILES, Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION).deny(Permission.MESSAGE_MANAGE).queue();
        return tc;
    }

    public ArrayList<Contest> getContests() {
        return contests;
    }

    public Contest checkChannel(Engine engine, TextChannel channel) {
        for (Contest c : contests) {
            if (c.getTc().equals(channel.getId()) && c.getGuild().equals(channel.getGuild().getId()))
                return c;
        }
        return null;
    }

    public class Contest implements Serializable {

        public static final long serialVersionUID = 42L;

        private String tc;
        private String guild;
        private int reward;
        private ArrayList<String> usersParticipate = new ArrayList<>();
        private ArrayList<String> userVoted = new ArrayList<>();
        private Date contestEnd;
        private boolean isOpen = true;

        public Contest(TextChannel tc, int reward, Date contestEnd) {
            this.tc = tc.getId();
            this.guild = tc.getGuild().getId();
            this.reward = reward;
            this.contestEnd = contestEnd;
        }

        public boolean isParticipant(String user) {
            for (String s : usersParticipate) {
                if (user.equals(s))
                    return true;
            }
            return false;
        }

        public boolean isVoter(String user) {
            for (String s : userVoted) {
                if (user.equals(s))
                    return true;
            }
            return false;
        }

        public void addUserParticipate(String user) {
            usersParticipate.add(user);
        }

        public void addUserVoted(String user) {
            userVoted.add(user);
        }

        public void removeUserVoted(String user) {
            userVoted.remove(user);
        }

        public void removeUserParticipate(String user) {
            usersParticipate.remove(user);
        }

        public TextChannel getTextChannel(Guild g) {
            return g.getTextChannelById(tc);
        }

        public TextChannel getTextChannel(JDA jda) {
            return getGuild(jda).getTextChannelById(tc);
        }

        public Guild getGuild(JDA jda) {
            return jda.getGuildById(guild);
        }

        public String getTc() {
            return tc;
        }

        public void setTc(String tc) {
            this.tc = tc;
        }

        public String getGuild() {
            return guild;
        }

        public void setGuild(String guild) {
            this.guild = guild;
        }

        public int getReward() {
            return reward;
        }

        public void setReward(int reward) {
            this.reward = reward;
        }

        public ArrayList<String> getUsersParticipate() {
            return usersParticipate;
        }

        public void setUsersParticipate(ArrayList<String> usersParticipate) {
            this.usersParticipate = usersParticipate;
        }

        public ArrayList<String> getUserVoted() {
            return userVoted;
        }

        public void setUserVoted(ArrayList<String> userVoted) {
            this.userVoted = userVoted;
        }

        public Date getContestEnd() {
            return contestEnd;
        }

        public void setContestEnd(Date contestEnd) {
            this.contestEnd = contestEnd;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
    }
}
