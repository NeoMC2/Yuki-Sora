package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

public class Platinum extends Metal {

    public Platinum() {
        setDescription("A metal that looks a little bit mystic");
        setImgUrl(Item.imageItemPath + "platin.PNG");
        setItemName("Platinum");
        setItemRarity(Rarity.Mystic);
    }
}
