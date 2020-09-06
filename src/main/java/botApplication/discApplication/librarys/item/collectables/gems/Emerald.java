package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Emerald extends Gem {

    public Emerald() {
        setImgUrl(Item.imageItemPath + "emerald.PNG");
        setItemName("Emerald");
        setItemRarity(Rarity.Legendary);
    }
}
