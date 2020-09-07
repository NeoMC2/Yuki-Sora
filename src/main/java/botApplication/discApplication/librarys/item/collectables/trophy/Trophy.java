package botApplication.discApplication.librarys.item.collectables.trophy;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Trophy extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    @Override
    public Item clone() {
        Trophy t = new Trophy();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        return t;
    }
}
