package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class Drop implements DungeonAction, Serializable {

    private final Engine engine;

    private final String dropText = "";

    private int quantity;

    public Drop(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        String msg = dropText;
        msg += "Wait, there is a chest in the middle of this room\n\n**You've got**\n\n";

        //TODO: make api request
        dungeon.caveActionFinished(false);
    }

    @Override
    public void generate() {
        int q = ThreadLocalRandom.current().nextInt(1, 100);
        int quantity;

        if (q > 90) {
            quantity = ThreadLocalRandom.current().nextInt(9, 15);
        } else if (q > 70) {
            quantity = ThreadLocalRandom.current().nextInt(5, 8);
        } else if (q > 60) {
            quantity = ThreadLocalRandom.current().nextInt(3, 5);
        } else {
            quantity = ThreadLocalRandom.current().nextInt(1, 2);
        }
        this.quantity = quantity;
    }
}
