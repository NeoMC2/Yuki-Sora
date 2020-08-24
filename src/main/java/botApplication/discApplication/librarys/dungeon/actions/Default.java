package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;

import java.io.Serializable;

public class Default implements DungeonAction, Serializable {

    private Engine engine;
    private String msg = "Seems like this is a normal cave with nothing special in it!";

    public Default(Engine engine) {
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
