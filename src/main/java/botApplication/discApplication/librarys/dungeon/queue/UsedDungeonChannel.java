package botApplication.discApplication.librarys.dungeon.queue;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UsedDungeonChannel {

    private DungeonChannelHandler ch;
    private Member m;
    private Guild g;

    public UsedDungeonChannel(DungeonChannelHandler ch, Member m, Guild g) {
        this.ch = ch;
        this.m = m;
        this.g = g;
    }

    public DungeonChannelHandler getCh() {
        return ch;
    }

    public void setCh(DungeonChannelHandler ch) {
        this.ch = ch;
    }

    public Member getM() {
        return m;
    }

    public void setM(Member m) {
        this.m = m;
    }

    public Guild getG() {
        return g;
    }

    public void setG(Guild g) {
        this.g = g;
    }

}
