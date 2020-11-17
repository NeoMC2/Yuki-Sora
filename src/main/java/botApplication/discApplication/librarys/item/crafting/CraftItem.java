package botApplication.discApplication.librarys.item.crafting;

import botApplication.discApplication.librarys.item.Item;

public class CraftItem {

    public Item item;
    public int amount = 1;
    public int state = -1;

    public CraftUsage craftUsage = CraftUsage.internUse;
    public int toolDestruction = 5;

    public CraftItem(Item item) {
        this.item = item;
    }

    public enum CraftUsage {
        internUse, externalUse
    }
}
