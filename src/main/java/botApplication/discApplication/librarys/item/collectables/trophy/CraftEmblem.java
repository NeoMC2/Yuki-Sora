package botApplication.discApplication.librarys.item.collectables.trophy;

import botApplication.discApplication.librarys.item.Item;

public class CraftEmblem extends Trophy {

    public CraftEmblem() {
        setImgUrl(Item.imageItemPath + "craftemblem.PNG");
        setItemName("Crafter Emblem");
        setItemRarity(Rarity.Mystic);
    }
}
