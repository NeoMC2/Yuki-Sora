package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.Item;
import core.Engine;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.Serializable;
import java.util.ArrayList;

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
        msg += "\n\n**You've got\n\n";

        for (Item item:drops) {
            msg += item.getItemName() + " (" + Item.rarityToString(item.getItemRarity()) + ")\n";
        }
        dungeon.caveActionFinished(false);
    }

    @Override
    public void generate() {
        Item i = new Item();
        i.setItemName("special stone...only available here :DDD");
        i.setItemRarity(Item.Rarity.Mystic);
        drops.add(i);
    }
}
