package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Copper extends Metal {

    public Copper() {
        setImgUrl(Item.imageItemPath + "copper.PNG");
        setItemName("Copper");
        setItemRarity(Rarity.Epic);
    }
}
