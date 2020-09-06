package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

public class Ruby extends Gem {

    public Ruby() {
        setImgUrl(Item.imageItemPath + "ruby.PNG");
        setItemName("Ruby");
        setItemRarity(Rarity.Legendary);
    }
}
