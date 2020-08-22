package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;

public class XPDrop implements DungeonAction, Serializable {

    private Engine engine;

    private String msg = "Seems like this is a XP Drop!";

    public XPDrop(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        dungeon.getTextChannel().sendMessage(msg).queue();
        dungeon.caveActionFinished(false);
    }

    @Override
    public void generate() {

    }
}
