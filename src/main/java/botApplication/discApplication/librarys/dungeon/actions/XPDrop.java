package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class XPDrop implements DungeonAction, Serializable {

    private Engine engine;

    public XPDrop(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        int xp = ThreadLocalRandom.current().nextInt(5, 60);
        dungeon.getTextChannel().sendMessage("You found a stone of wisdom! Your Monster got " + xp + " xp!").queue();
        dungeon.getM().earnXP(xp, dungeon.getEngine(), dungeon.getUser());
        dungeon.caveActionFinished(false);
    }

    @Override
    public void generate() {

    }
}
