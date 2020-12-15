package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MonsterFight implements DungeonAction, Serializable {

    public Engine engine;
    public Dungeon d;
    private final String msg = "Seems like here is a monster!";

    public MonsterFight(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        //TODO: make this
    }

    @Override
    public void generate() {
    }
}
