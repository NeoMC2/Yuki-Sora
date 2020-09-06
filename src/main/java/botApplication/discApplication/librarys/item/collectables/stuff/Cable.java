package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Cable extends Stuff {

    public Cable() {
        setImgUrl(Item.imageItemPath + "cable.PNG");
        setItemName("Cable");
        setItemRarity(Rarity.Normal);
    }
}
