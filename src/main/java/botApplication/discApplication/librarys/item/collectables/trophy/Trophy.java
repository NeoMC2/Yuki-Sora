package botApplication.discApplication.librarys.item.collectables.trophy;

import botApplication.discApplication.librarys.item.Item;

public class Trophy extends Item {

    @Override
    public Item clone() {
        Trophy t = new Trophy();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setItemState(getItemState());
        return t;
    }
}
