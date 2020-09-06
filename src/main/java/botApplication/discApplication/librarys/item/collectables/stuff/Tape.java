package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Tape extends Stuff{

    public Tape() {
        setImgUrl(Item.imageItemPath + "tape.PNG");
        setItemName("Tape");
        setItemRarity(Rarity.Normal);
    }
}
