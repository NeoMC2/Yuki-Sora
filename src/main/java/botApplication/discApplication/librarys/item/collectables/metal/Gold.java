package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Gold extends Metal {

    public Gold() {
        setImgUrl(Item.imageItemPath + "gold.PNG");
        setItemName("Gold");
        setItemRarity(Rarity.Legendary);
    }
}
