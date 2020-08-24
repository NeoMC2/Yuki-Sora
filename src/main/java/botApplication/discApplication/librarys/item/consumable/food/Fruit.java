package botApplication.discApplication.librarys.item.consumable.food;

import botApplication.discApplication.librarys.item.monsters.Monster;

public class Fruit extends Food {

    public Fruit() {
        setType(Monster.getRandomMonsterType());
        setItemRarity(Rarity.Epic);
        setItemName(getType().name() + " Fruit");
    }
}
