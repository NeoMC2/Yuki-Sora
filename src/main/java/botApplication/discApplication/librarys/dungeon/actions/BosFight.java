package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.Map.Entry;

public class BosFight implements DungeonAction, Serializable {
    private Engine engine;

    private String msg = "Seems like this is a bosfight!";

    public BosFight(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        dungeon.getTextChannel().sendMessage(msg).queue();
        dungeon.caveActionFinished(true);
    }

    @Override
    public void generate() {
    }
}
