package botApplication.discApplication.librarys.item.collectables.trophy;

import botApplication.discApplication.librarys.item.Item;

public class CraftEmblem extends Trophy {

    public CraftEmblem() {
        setDescription("The little text down there says \"Crafting is stolen from minecraft\" I don't know what this means");
        setImgUrl(Item.imageItemPath + "craftemblem.PNG");
        setItemName("Crafter Emblem");
        setItemRarity(Rarity.Mystic);
    }
}
