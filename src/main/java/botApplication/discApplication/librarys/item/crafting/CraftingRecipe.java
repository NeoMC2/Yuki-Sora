package botApplication.discApplication.librarys.item.crafting;

import botApplication.discApplication.librarys.item.Item;

import java.util.ArrayList;

public class CraftingRecipe {

    public ArrayList<CraftItem> ingredients = new ArrayList<>();
    public Item result;
    public int resAmount = 1;

    public Item craft(ArrayList<Item> inv) throws Exception {
        ArrayList<Item> foundCraft = new ArrayList<>();
        ArrayList<CraftItem> found = new ArrayList<>();
        for(Item i: inv){
            for (CraftItem in : ingredients){
                if(!i.getItemName().equals(in.item.getItemName()))
                    continue;

                if(in.state!=-1)
                    if(in.state != i.getItemState())
                        continue;


                    if(in.amount > 1){
                        int amountF = 0;
                        ArrayList<Item> f = new ArrayList<>();
                        for(Item itt: inv) {
                            if(amountF == in.amount)
                                break;

                            if(!i.getItemName().equals(in.item.getItemName()))
                                continue;

                            if(in.state!=-1)
                                if(in.state != i.getItemState())
                                    continue;

                                amountF ++;
                                f.add(itt);
                        }

                        if(amountF == in.amount){
                            foundCraft.addAll(f);
                            found.add(in);
                        }

                    } else {
                        foundCraft.add(i);
                        found.add(in);
                    }
            }
        }

        if(found.size() == ingredients.size()){
            for (Item it:foundCraft){
                inv.remove(it);
            }

            return result.clone();
        }

        throw new Exception("Cant craft Item, not all items in inventory!");
    }
}
