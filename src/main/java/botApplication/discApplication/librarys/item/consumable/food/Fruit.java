package botApplication.discApplication.librarys.item.consumable.food;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.Monster;

public class Fruit extends Food {

    public Fruit() {
        setType(Monster.getRandomMonsterType());
        setImgUrl(Item.imageItemPath + getType().name() + "-fruit.PNG");
        setItemRarity(Rarity.Epic);
        setItemName(getType().name() + " Fruit");
    }
}
