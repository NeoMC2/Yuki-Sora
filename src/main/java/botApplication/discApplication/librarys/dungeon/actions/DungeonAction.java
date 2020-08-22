package botApplication.discApplication.librarys.dungeon.actions;
import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import net.dv8tion.jda.api.entities.TextChannel;

public interface DungeonAction {
    public void action(Dungeon dungeon);
    public void generate();
}
