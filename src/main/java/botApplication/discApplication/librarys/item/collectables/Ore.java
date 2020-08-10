package botApplication.discApplication.librarys.item.collectables;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Ore extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    public enum OreType implements Serializable {
        Copper, Iron, Gold, Platinum
    }
}
