package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Iron extends Metal {

    public Iron() {
        setImgUrl(Item.imageItemPath + "iron.PNG");
        setItemName("Iron");
        setItemRarity(Rarity.Epic);
    }
}
