package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Stick extends Stuff {

    public Stick() {
        setDescription("A small tree :3");
        setImgUrl(Item.imageItemPath + "stick.PNG");
        setItemName("Stick");
        setItemRarity(Rarity.Normal);
    }
}
