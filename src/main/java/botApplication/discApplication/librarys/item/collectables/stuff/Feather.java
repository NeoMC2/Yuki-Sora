package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Feather extends Stuff {

    public Feather() {
        setImgUrl(Item.imageItemPath + "feather.PNG");
        setItemName("Feather");
        setItemRarity(Rarity.Normal);
    }
}
