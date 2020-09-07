package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Aluminium extends Metal {

    public Aluminium() {
        setDescription("A soft metal that shines white");
        setImgUrl(Item.imageItemPath + "aluminium.PNG");
        setItemName("Aluminium");
        setItemRarity(Rarity.Normal);
    }
}
