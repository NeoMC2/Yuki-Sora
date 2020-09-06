package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Glass extends Stuff {

    public Glass() {
        setImgUrl(Item.imageItemPath + "glass.PNG");
        setItemName("Glass");
        setItemRarity(Rarity.Normal);
    }
}
