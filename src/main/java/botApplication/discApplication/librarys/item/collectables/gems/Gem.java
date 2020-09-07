package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Gem extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    @Override
    public Item clone() {
        Gem t = new Gem();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        return t;
    }
}
