package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.ItemStorage;
import core.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Drop implements DungeonAction, Serializable {

    private Engine engine;

    private ArrayList<Item> drops = new ArrayList<>();
    private String dropText = "";

    public Drop(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        String msg = dropText;
        msg += "Wait, there is a chest in the middle of this room\n\n**You've got**\n\n";

        for (Item item : drops) {
            msg += item.getItemName() + " (" + Item.rarityToString(item.getItemRarity()) + ")\n";
        }
        dungeon.getTextChannel().sendMessage(msg).queue();
        drops.forEach(e -> dungeon.foundItem(e));
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

        for (int i = 0; i < quantity; i++) {
            Item item = null;
            int t = ThreadLocalRandom.current().nextInt(0, 100);

            if (t > 80) {
                item = ItemStorage.getRandomGem();
            } else if (t > 70) {
                item = ItemStorage.getRandomMetal();
            } else if (t > 60) {
                item = ItemStorage.getRandomFood();
            } else {
                item = ItemStorage.getRandomStuff();
            }

            if (item != null)
                drops.add(item);
        }
    }
}
