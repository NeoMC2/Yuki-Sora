package botApplication.discApplication.librarys.transaktion;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private String itemName;
    private Rarity itemRarity;
    private String imgUrl;

    public enum Rarity {
        Normal, Epic, Legendary, Mystic
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Rarity getItemRarity() {
        return itemRarity;
    }

    public void setItemRarity(Rarity itemRarity) {
        this.itemRarity = itemRarity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
