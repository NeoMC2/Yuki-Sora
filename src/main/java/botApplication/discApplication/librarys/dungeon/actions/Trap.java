package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;

import java.util.concurrent.ThreadLocalRandom;

public class Trap implements DungeonAction {

    private final Engine engine;

    private int dmg = 0;

    public Trap(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        dmg = ThreadLocalRandom.current().nextInt(3, 20);
        dmg = (dmg * (dungeon.getM().getEvolveLevel() / 50 + 1)) / 2;
        dungeon.getM().setHp(dungeon.getM().getHp() - dmg);
        if (dungeon.getM().getHp() <= 0)
            dungeon.getM().setHp(1);
        dungeon.caveActionFinished(false);
        dungeon.getTextChannel().sendMessage("Seems like this is a trap! Your monster got " + dmg + " damage and has " + dungeon.getM().getHp() + " hp left!").queue();
    }

    @Override
    public void generate() {
    }
}