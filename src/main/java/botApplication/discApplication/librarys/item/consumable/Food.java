package botApplication.discApplication.librarys.item.consumable;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.Monster;

import java.io.Serializable;

public class Food extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private Monster.MonsterType type;

}
