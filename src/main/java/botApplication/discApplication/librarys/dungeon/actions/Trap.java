package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;

public class Trap implements DungeonAction {

    private Engine engine;

    private String msg = "Seems like this is a trap!";

    public Trap(Engine engine) {
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
