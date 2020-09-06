package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Silver extends Metal {

    public Silver() {
        setImgUrl(Item.imageItemPath + "silver.PNG");
        setItemName("Silver");
        setItemRarity(Rarity.Legendary);
    }
}
