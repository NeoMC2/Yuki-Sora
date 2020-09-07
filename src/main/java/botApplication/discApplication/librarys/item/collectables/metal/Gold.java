package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Gold extends Metal {

    public Gold() {
        setDescription("A metal that shines bright and gives you a feeling of being rich");
        setImgUrl(Item.imageItemPath + "gold.PNG");
        setItemName("Gold");
        setItemRarity(Rarity.Legendary);
    }
}
