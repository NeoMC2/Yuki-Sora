package botApplication.discApplication.commands;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class DiscCmdRockPaperScissors implements DiscCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        return true;
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, DiscApplicationUser user, Engine engine) {
        Rps u;
        switch (args[0]) {
            case "r":
            case "rock":
                u = Rps.Rock;
                break;

            case "p":
            case "paper":
                u = Rps.Paper;
                break;

            case "s":
            case "scissors":
                u = Rps.Scissors;
                break;

            default:
                return;
        }

        Rps pc;
        switch (ThreadLocalRandom.current().nextInt(0, 2)) {
            case 0:
                pc = Rps.Paper;
                break;

            case 1:
                pc = Rps.Rock;
                break;

            case 2:
                pc = Rps.Scissors;
                break;

            default:
                return;
        }

        int asw = 2;
        if (u == pc) {
            asw = 0;

        } else {
            if (u == Rps.Scissors) {
                if (pc == Rps.Paper) {
                    asw = 1;
                }
            }

            if (u == Rps.Rock) {
                if (pc == Rps.Scissors) {
                    asw = 1;

                }
            }

            if (u == Rps.Paper) {
                if (pc == Rps.Rock) {
                    asw = 1;
                }
            }
        }

        String answer = "";
        Color c = Color.cyan;
        if(asw == 0){
            answer = "Draw :woman_shrugging:";
            c = Color.yellow;
        } else if (asw == 1){
            answer = "You won :confetti_ball:";
            c = Color.green;
        } else if (asw == 2){
            answer = "You loose :boom:";
            c = Color.red;
        }

        EmbedBuilder em = new EmbedBuilder()
                .setAuthor(event.getMember().getUser().getName() + " against Yuki-Sora", null, event.getMember().getUser().getAvatarUrl())
                .setDescription(getEmojiFromRps(u) + "  :crossed_swords:  " + getEmojiFromRps(pc) + "\n\n" + answer)
                .setColor(c);
        event.getChannel().sendMessage(em.build()).queue();
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

    private enum Rps {
        Rock, Paper, Scissors
    }

    private String getEmojiFromRps(Rps rps){
        if(rps == Rps.Paper){
            return ":roll_of_paper:";
        } else if (rps == Rps.Rock){
            return ":mountain:";
        } else if (rps == Rps.Scissors){
            return ":scissors:";
        } else {
            return  "mö";
        }
    }
}
