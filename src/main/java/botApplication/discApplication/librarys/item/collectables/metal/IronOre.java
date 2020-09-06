package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;

public class IronOre extends Metal implements Cookable {

    public IronOre() {
        setImgUrl(Item.imageItemPath + "iron-ore.PNG");
        setItemName("Iron Ore");
        setItemRarity(Rarity.Epic);
    }

    @Override
    public int cookTime() {
        return 5;
    }

    @Override
    public Item result() {
        return new Iron();
    }
}
