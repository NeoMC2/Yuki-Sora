package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.crafting.Cookable;

public class CopperOre extends Metal implements Cookable {

    public CopperOre() {
        setDescription("A peace of stone with some shining metal lines in it");
        setImgUrl(Item.imageItemPath + "copper-ore.PNG");
        setItemName("Copper Ore");
        setItemRarity(Item.Rarity.Epic);
    }

    @Override
    public int cookTime() {
        return 5;
    }

    @Override
    public Item result() {
        return new Copper();
    }
}
