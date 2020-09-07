package botApplication.discApplication.librarys.item.equipment;

import botApplication.discApplication.librarys.item.Item;

public class Equipment extends Item {

    private final int buff;
    private final int extraLifePoints;
    private final int minLevel;

    public Equipment(int buff, int extraLifePoints, int minLevel) {
        this.buff = buff;
        this.extraLifePoints = extraLifePoints;
        this.minLevel = minLevel;
    }
}
