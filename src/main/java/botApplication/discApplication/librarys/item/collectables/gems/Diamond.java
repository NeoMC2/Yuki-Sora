package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Diamond extends Gem {

    public Diamond() {
        setImgUrl(Item.imageItemPath + "diamond.PNG");
        setItemName("Diamond");
        setItemRarity(Rarity.Legendary);
    }
}
