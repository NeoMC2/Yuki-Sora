package botApplication.discApplication.librarys.item.tools;

import botApplication.discApplication.librarys.item.Item;

import java.util.ArrayList;

public class Tools extends Item {

    private int destruction = 100;
    private ArrayList<Item> repair = new ArrayList<>();


    @Override
    public Item clone() {
        Tools t = new Tools();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setItemState(getItemState());
        t.setDestruction(destruction);

        ArrayList<Item> nRep = new ArrayList<>();
        repair.forEach(e -> nRep.add(e));
        t.setRepair(nRep);
        return t;
    }

    public int getDestruction() {
        return destruction;
    }

    public void setDestruction(int destruction) {
        this.destruction = destruction;
    }

    public ArrayList<Item> getRepair() {
        return repair;
    }

    public void setRepair(ArrayList<Item> repair) {
        this.repair = repair;
    }
}
