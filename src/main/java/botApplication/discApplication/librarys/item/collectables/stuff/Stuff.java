package botApplication.discApplication.librarys.item.collectables.stuff;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Stuff extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    @Override
    public Item clone() {
        Stuff t = new Stuff();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setItemState(getItemState());
        return t;
    }
}
