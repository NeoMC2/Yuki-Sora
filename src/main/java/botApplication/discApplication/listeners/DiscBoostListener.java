package botApplication.discApplication.listeners;

import botApplication.discApplication.librarys.DiscApplicationServer;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.utils.DiscUtilityBase;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import core.Engine;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;

public class DiscBoostListener extends ListenerAdapter {

    private Engine engine;

    public DiscBoostListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
        update(event.getNewBoostCount() > event.getOldBoostCount(), event.getGuild());
    }

    public void update(Boolean bool, Guild guild){
        DiscApplicationServer server = DiscUtilityBase.lookForServer(guild, engine);
        ArrayList<Member> boosters = new ArrayList<>();
        ArrayList<DiscApplicationUser> boostersUsers = new ArrayList<>();
        ArrayList<String> boosterIds = new ArrayList<>();

        boosters.add(guild.getMemberById("377469972949237760"));
        boostersUsers.add(DiscUtilityBase.lookForUserById(boosters.get(0).getUser(), engine));
        boosterIds.add(boosters.get(0).getUser().getId());


        for (Member m:guild.getBoosters()) {
            boosterIds.add(m.getUser().getId());
            boosters.add(m);
            DiscApplicationUser usr = null;
            try {
                usr = DiscUtilityBase.lookForUserById(m.getUser(), engine);
            } catch (Exception ignored){
            }
            boostersUsers.add(usr);
        }

        if(bool){
            for (int i = 0; i < boosters.size(); i++) {
                DiscApplicationUser usr = boostersUsers.get(i);
                //if its a new booster
                if(!usr.isBooster()){
                    usr.setBooster(true);
                    EmbedBuilder b = new EmbedBuilder().setColor(Color.MAGENTA).setAuthor(guild.getName() + " says thank you!", null, boosters.get(0).getUser().getAvatarUrl()).setDescription("As a little reward for your support you've got the donation role on " + guild.getName() + ".\n\n" +
                            "Furthermore you've got your own channel in the donation category!\n\nYou can invite members by typing `!inv <member...>`\nRemove them by typing `!rem <member...>`\nChange the name by typing `!name <name>`\nChange the channel type by typing either `!vc` or `!tc`\n\nThanks again and have fun on our discord, your " + guild.getName() + " team :tada: :confetti_ball: :tada: :confetti_ball: ");
                    boosters.get(i).getUser().openPrivateChannel().complete().sendMessage(b.build()).queue();
                    try {
                        guild.addRoleToMember(boosters.get(i), guild.getRoleById(server.getBoosterRoleId())).queue();
                    } catch (Exception ignored){
                        if(engine.getProperties().debug)
                            ignored.printStackTrace();
                    }
                    TextChannel tc = guild.createTextChannel(boosters.get(i).getUser().getName(), guild.getCategoryById(server.getBoosterCategoryId())).complete();
                    tc.getManager().clearOverridesRemoved().queue();
                    tc.createPermissionOverride(boosters.get(i)).setAllow(Permission.ALL_TEXT_PERMISSIONS).queue();
                    usr.addBoosterChan(tc.getId());
                }
            }
        }

        for (DiscApplicationUser usr:engine.getDiscEngine().getFilesHandler().getUsers().values()){
            Member u = guild.getMemberById(usr.getUserId());
            if(usr.isBooster() && boosterIds.contains(u.getUser().getId())){
                guild.removeRoleFromMember(u, guild.getRoleById(server.getBoosterRoleId())).queue();
                for (String s: usr.getBoosterChans()){
                    VoiceChannel vc = null;
                    TextChannel tc = null;

                    try {
                        vc = guild.getVoiceChannelById(s);
                    } catch (Exception e){
                    }

                    if(vc == null)
                        try {
                            tc = guild.getTextChannelById(s);
                        } catch (Exception e){
                        }

                    if(vc != null)
                        vc.delete().queue();

                    if(tc != null)
                        tc.delete().queue();
                }
                usr.setBooster(false);
                usr.clearBoosterChans();
            }
        }
    }
}
