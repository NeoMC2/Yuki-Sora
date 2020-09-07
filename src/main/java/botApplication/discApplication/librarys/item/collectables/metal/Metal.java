package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Metal extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    @Override
    public Item clone() {
        Metal t = new Metal();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        return t;
    }
}
