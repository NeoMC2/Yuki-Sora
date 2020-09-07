package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Cable extends Stuff {

    public Cable() {
        setDescription("Could be from an old radio or something");
        setImgUrl(Item.imageItemPath + "cable.PNG");
        setItemName("Cable");
        setItemRarity(Rarity.Normal);
    }
}
