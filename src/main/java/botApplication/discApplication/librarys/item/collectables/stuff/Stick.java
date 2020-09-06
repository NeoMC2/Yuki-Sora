package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Stick extends Stuff {

    public Stick() {
        setImgUrl(Item.imageItemPath + "stick.PNG");
        setItemName("Stick");
        setItemRarity(Rarity.Normal);
    }
}
