package botApplication.discApplication.librarys.item.collectables;

import botApplication.discApplication.librarys.item.Item;

import java.io.Serializable;

public class Gem extends Item implements Serializable {

    public enum GemType {
        Diamond, Ruby, Sapphire, Lapislazuli, Emeralde
    }
}
