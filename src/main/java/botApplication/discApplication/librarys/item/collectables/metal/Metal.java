package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Metal extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private Form form = Form.Ore;

    //0 = fluid, 1 = soft, 2 = soft - hard, 3 = hard, 4 = superhard
    private int hardness = 1;
    private int cookTime = 5;

    private String defItemName;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        if (form == Form.Ingot) {
            setItemState(1);
            setItemName(defItemName + " ingot");
        } else {
            setItemState(0);
            setItemName(defItemName + " ore");
        }

        this.form = form;
    }

    @Override
    public void setItemName(String itemName) {
        if (defItemName == null) {
            defItemName = itemName;
            setForm(form);
        }
        super.setItemName(itemName);
    }

    @Override
    public Item clone() {
        Metal t = new Metal();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setItemState(getItemState());
        t.setCookTime(cookTime);
        t.setHardness(hardness);
        return t;
    }

    public int getHardness() {
        return hardness;
    }

    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public enum Form {
        Ore, Ingot
    }
}
