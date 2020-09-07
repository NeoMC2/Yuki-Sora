package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

public class MetalScrew extends Stuff {

    public MetalScrew() {
        setDescription("Could be from an old radio");
        setImgUrl(Item.imageItemPath + "metal-screw.PNG");
        setItemName("MetalScrew");
        setItemRarity(Rarity.Normal);
    }
}
