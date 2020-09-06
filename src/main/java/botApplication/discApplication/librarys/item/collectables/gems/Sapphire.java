package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Sapphire extends Gem {

    public Sapphire() {
        setImgUrl(Item.imageItemPath + "sapphire.PNG");
        setItemName("Sapphire");
        setItemRarity(Rarity.Legendary);
    }
}
