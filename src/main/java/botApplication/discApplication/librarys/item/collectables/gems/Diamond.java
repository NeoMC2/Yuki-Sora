package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Diamond extends Gem {

    public Diamond() {
        setDescription("A stone that shines so brightly that you have to close your eyes");
        setImgUrl(Item.imageItemPath + "diamond.PNG");
        setItemName("Diamond");
        setItemRarity(Rarity.Legendary);
    }
}
