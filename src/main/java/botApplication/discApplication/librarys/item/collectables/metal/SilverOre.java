package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;

public class SilverOre extends Metal implements Cookable {

    public SilverOre() {
        setImgUrl(Item.imageItemPath + "silver-ore.PNG");
        setItemName("Silver Ore");
        setItemRarity(Rarity.Legendary);
    }

    @Override
    public int cookTime() {
        return 10;
    }

    @Override
    public Item result() {
        return new Silver();
    }
}
