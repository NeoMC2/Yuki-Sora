package botApplication.discApplication.librarys.item.collectables.metal;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Metal extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private Form form = Form.Ore;

    //0 = fluid, 1 = soft, 2 = soft - hard, 3 = hard, 4 = superhard
    private int hardness = 1;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public int getHardness() {
        return hardness;
    }

    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    public enum Form {
        Ore, Ingot
    }
}
