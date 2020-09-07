package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Glass extends Stuff {

    public Glass() {
        setDescription("A broken peace of glass, maybe from a window");
        setImgUrl(Item.imageItemPath + "glass.PNG");
        setItemName("Glass");
        setItemRarity(Rarity.Normal);
    }
}
