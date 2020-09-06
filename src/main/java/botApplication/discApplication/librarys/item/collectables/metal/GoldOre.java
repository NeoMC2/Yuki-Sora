package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;

public class GoldOre extends Metal implements Cookable {

    public GoldOre() {
        setImgUrl(Item.imageItemPath + "gold-ore.PNG");
        setItemName("Gold Ore");
        setItemRarity(Rarity.Legendary);
    }

    @Override
    public int cookTime() {
        return 5;
    }

    @Override
    public Item result() {
        return new Gold();
    }
}
