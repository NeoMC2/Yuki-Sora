package botApplication.discApplication.librarys.item.collectables.gems;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Gem extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private Form form = Form.Hard;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public enum Form {
        Dust, Hard
    }

    @Override
    public Item clone() {
        Gem t = new Gem();
        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setItemState(getItemState());
        t.setForm(form);
        return t;
    }
}
