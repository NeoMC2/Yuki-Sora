package botApplication.discApplication.librarys.item;

import java.awt.*;
import java.io.Serializable;

public class Item implements Serializable, Cloneable {

    private static final long serialVersionUID = 42L;

    private String itemName;
    private Rarity itemRarity;
    private String imgUrl;
    private int itemState;
    private String description;

    public static final String imageItemPath = "http://mindcollaps.de/yukisora/assets/items/";

    public static Rarity stringToRarity(String s) {
        switch (s.toLowerCase()) {
            case "normal":
                return Rarity.Normal;
            case "epic":
                return Rarity.Epic;
            case "legendary":
                return Rarity.Legendary;
            case "mystic":
                return Rarity.Mystic;
        }
        return null;
    }

    public static Color rarityToColor(Rarity r) {
        switch (r) {

            case Normal:
                return Color.GRAY;
            case Epic:
                return Color.MAGENTA;
            case Legendary:
                return Color.ORANGE;
            case Mystic:
                return Color.decode("#11EA7B");
        }
        return Color.CYAN;
    }

    public static String rarityToString(Rarity r) {
        switch (r) {

            case Normal:
                return "normal";
            case Epic:
                return "epic";
            case Legendary:
                return "legendary";
            case Mystic:
                return "mystic";
        }
        return null;
    }

    public static int rarityToInt(Rarity r) {
        switch (r) {

            case Normal:
                return 0;
            case Epic:
                return 1;
            case Legendary:
                return 2;
            case Mystic:
                return 3;
        }
        return 0;
    }

    public static int rarityToMarketValue(Rarity r) {
        switch (r) {

            case Normal:
                return 5;
            case Epic:
                return 20;
            case Legendary:
                return 60;
            case Mystic:
                return 300;
        }
        return 0;
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

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item clone() {
        Item t = new Item();
        t.setImgUrl(imgUrl);
        t.setItemName(itemName);
        t.setItemRarity(itemRarity);
        t.setItemState(itemState);
        return t;
    }

    public enum Rarity {
        Normal, Epic, Legendary, Mystic
    }
}
