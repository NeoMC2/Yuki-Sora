package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class Cobweb extends Stuff {

    public Cobweb() {
        setDescription("Those things that come from spiders\n");
        setImgUrl(Item.imageItemPath + "cobweb.PNG");
        setItemName("Cobweb");
        setItemRarity(Rarity.Normal);
    }
}
