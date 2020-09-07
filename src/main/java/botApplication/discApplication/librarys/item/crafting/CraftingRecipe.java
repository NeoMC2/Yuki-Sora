package botApplication.discApplication.librarys.item.crafting;

import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.tools.Tools;

import java.util.ArrayList;

public class CraftingRecipe {

    public ArrayList<CraftItem> ingredients = new ArrayList<>();
    public Item result;
    public int resAmount = 1;

    public Item craft(ArrayList<Item> inv) throws Exception {
        ArrayList<Item> foundCraft = new ArrayList<>();
        ArrayList<CraftItem> found = new ArrayList<>();
        ArrayList<Tools> tools = new ArrayList<>();

        for (Item i : inv) {
            boolean had = false;
            for (CraftItem in : ingredients) {
                if (!i.getItemName().equals(in.item.getItemName()))
                    continue;

                if (in.craftUsage == CraftItem.CraftUsage.externalUse) {
                    tools.add((Tools) i);
                } else if (in.amount > 1) {
                    int amountF = 0;
                    ArrayList<Item> f = new ArrayList<>();
                    for (Item itt : inv) {
                        if (amountF == in.amount)
                            break;

                        if (!i.getItemName().equals(in.item.getItemName()))
                            continue;

                        amountF++;
                        f.add(itt);
                    }

                    if (amountF == in.amount) {
                        foundCraft.addAll(f);
                        found.add(in);
                        had = true;
                    }

                } else {
                    foundCraft.add(i);
                    found.add(in);
                    had = true;
                }
            }
            if (!had)
                throw new Exception("Not all Items in Inventory");
        }

        for (Item it : foundCraft) {
            inv.remove(it);
            tools.forEach(e -> e.setDestruction(e.getDestruction() - 5));
        }

        return result.clone();
    }
}
