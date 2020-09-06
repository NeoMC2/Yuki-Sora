package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;

public class PlatinumOre extends Metal implements Cookable {

    public PlatinumOre() {
        setImgUrl(Item.imageItemPath + "platin-ore.PNG");
        setItemName("Platinum Ore");
        setItemRarity(Rarity.Mystic);
    }

    @Override
    public int cookTime() {
        return 30;
    }

    @Override
    public Item result() {
        return new Platinum();
    }
}
