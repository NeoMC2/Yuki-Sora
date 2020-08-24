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
}
