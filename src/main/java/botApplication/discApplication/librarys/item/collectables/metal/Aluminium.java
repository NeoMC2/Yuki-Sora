package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Aluminium extends Metal {

    public Aluminium() {
        setImgUrl(Item.imageItemPath + "aluminium.PNG");
        setItemName("Aluminium");
        setItemRarity(Rarity.Normal);
    }
}
