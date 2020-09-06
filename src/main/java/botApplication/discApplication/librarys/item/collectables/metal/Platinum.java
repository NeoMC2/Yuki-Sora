package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Platinum extends Metal {

    public Platinum() {
        setImgUrl(Item.imageItemPath + "platin.PNG");
        setItemName("Platinum");
        setItemRarity(Rarity.Mystic);
    }
}
