package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Sapphire extends Gem {

    public Sapphire() {
        setDescription("A stone with the color of the ocean");
        setImgUrl(Item.imageItemPath + "sapphire.PNG");
        setItemName("Sapphire");
        setItemRarity(Rarity.Legendary);
    }
}
