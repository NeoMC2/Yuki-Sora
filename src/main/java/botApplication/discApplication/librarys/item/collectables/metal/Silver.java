package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Silver extends Metal {

    public Silver() {
        setDescription("A metal that looks like its very valuable but not really useful");
        setImgUrl(Item.imageItemPath + "silver.PNG");
        setItemName("Silver");
        setItemRarity(Rarity.Legendary);
    }
}
