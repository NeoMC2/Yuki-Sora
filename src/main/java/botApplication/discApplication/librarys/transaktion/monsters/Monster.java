package botApplication.discApplication.librarys.transaktion.monsters;

import botApplication.discApplication.librarys.transaktion.Item;

import javax.naming.directory.Attributes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Monster extends Item implements Serializable {

    private static final long serialVersionUID = 42L;

    private ArrayList<Attack> attacks = new ArrayList<>();
    private Attack a1;
    private Attack a2;
    private Attack a3;
    private Attack a4;
    private int dv = ThreadLocalRandom.current().nextInt(0, 15);
    private int baseHp;
    private int hp;
    private int maxHp;
    private int level = 1;
    private int xp;
    private ArrayList<MonsterType> monsterTypes = new ArrayList<>();

    public enum MonsterType {
        Psycho, Geist, Unlicht, Drache, Stahl, Fee, Normal, Feuer, Wasser, Elektro, Pflanze, Flug, Kaefer, Gift, Gestein, Boden, Kampf, Eis
    }

    public int attack(Attack attack, Monster enemy){
        double dmg = ((level * (2/5) + 2 ) + attack.getBaseDamage()) * calculateAttackEfficiency(monsterTypes, enemy.getMonsterTypes());
        enemy.setHp((int) (enemy.getHp() - dmg));
        return (int) dmg;
    }

    public void earnXP(int xp){
        this.xp+=xp;
        int levelUpXp = level * 10;
        if(xp >= levelUpXp){
            this.xp-=levelUpXp;
            level++;
            int newKp = (((2 * baseHp + dv) * level)/100)+ level + 10;
            hp = hp + (newKp - maxHp);
            maxHp = newKp;
        }
    }

    /**
     *
     * @return 0 no extra dmg, 1 extra dmg, -1 extra defense, -2 cant attack
     */
    public double calculateAttackEfficiency(ArrayList<MonsterType> typesAttack, ArrayList<MonsterType> typesDefend){
        int strength = 0;
        for (MonsterType own:typesAttack) {
            for (MonsterType enemy:typesDefend) {
                switch (getAttackEfficiency(own, enemy)){

                    case Strength:
                        strength++;
                        break;
                    case Weakness:
                        strength--;
                        break;
                    case CantAttack:
                        strength= -2;
                        break;
                }
            }
        }
        if(strength <= -2){
            return 0;
        } else if (strength <= -1){
            return 0.5;
        } else if (strength >=1){
            return 2;
        } else {
            return 1;
        }
    }

    public Efficiency getAttackEfficiency(MonsterType attacker, MonsterType defender){
        switch (attacker){

            case Psycho:
                switch (defender){
                    case Kampf:
                    case Gift:
                        return Efficiency.Strength;

                    case Stahl:
                    case Psycho:
                        return Efficiency.Weakness;

                    case Unlicht:
                        return Efficiency.CantAttack;
                }
                break;

            case Geist:
                switch (defender){
                    case Normal:
                        return Efficiency.CantAttack;

                    case Geist:
                    case Psycho:
                        return Efficiency.Strength;

                    case Unlicht:
                        return Efficiency.Weakness;
                }
                break;

            case Unlicht:
                switch (defender){
                    case Kampf:
                    case Unlicht:
                    case Fee:
                        return Efficiency.Weakness;

                    case Geist:
                    case Psycho:
                        return Efficiency.Strength;
                }
                break;

            case Drache:
                switch (defender){
                    case Stahl:
                        return Efficiency.Weakness;

                    case Drache:
                        return Efficiency.Strength;

                    case Fee:
                        return Efficiency.CantAttack;
                }
                break;

            case Stahl:
                switch (defender){
                    case Stahl:
                    case Feuer:
                    case Wasser:
                    case Elektro:
                        return Efficiency.Weakness;

                    case Eis:
                    case Gestein:
                    case Fee:
                        return Efficiency.Strength;
                }
                break;

            case Fee:
                switch (defender){
                    case Kampf:
                    case Drache:
                    case Unlicht:
                        return Efficiency.Strength;

                    case Gift:
                    case Stahl:
                    case Feuer:
                        return Efficiency.Weakness;
                }
                break;

            case Normal:
                switch (defender){
                    case Gestein:
                    case Stahl:
                        return Efficiency.Weakness;

                    case Geist:
                        return Efficiency.CantAttack;
                }
                break;

            case Feuer:
                switch (defender){
                    case Gestein:
                    case Feuer:
                    case Wasser:
                    case Drache:
                        return Efficiency.Weakness;

                    case Kaefer:
                    case Stahl:
                    case Pflanze:
                    case Eis:
                        return Efficiency.Strength;
                }
                break;

            case Wasser:
                switch (defender){
                    case Boden:
                    case Gestein:
                    case Feuer:
                        return Efficiency.Strength;

                    case Wasser:
                    case Pflanze:
                    case Drache:
                        return Efficiency.Weakness;
                }
                break;

            case Elektro:
                switch (defender){
                    case Flug:
                    case Wasser:
                        return Efficiency.Strength;

                    case Boden:
                        return Efficiency.CantAttack;

                    case Pflanze:
                    case Elektro:
                    case Drache:
                        return Efficiency.Weakness;
                }
                break;

            case Pflanze:
                switch (defender){
                    case Flug:
                    case Gift:
                    case Kaefer:
                    case Stahl:
                    case Feuer:
                    case Pflanze:
                    case Drache:
                        return Efficiency.Weakness;

                    case Boden:
                    case Gestein:
                    case Wasser:
                        return Efficiency.Strength;
                }
                break;

            case Flug:
                switch (defender){
                    case Kampf:
                    case Kaefer:
                    case Pflanze:
                        return Efficiency.Strength;

                    case Gestein:
                    case Stahl:
                    case Elektro:
                        return Efficiency.Weakness;
                }
                break;

            case Kaefer:
                switch (defender){
                    case Kampf:
                    case Flug:
                    case Gift:
                    case Geist:
                    case Stahl:
                    case Feuer:
                    case Fee:
                        return Efficiency.Weakness;

                    case Pflanze:
                    case Psycho:
                    case Unlicht:
                        return Efficiency.Strength;
                }
                break;

            case Gift:
                switch (defender){
                    case Gift:
                    case Boden:
                    case Gestein:
                    case Geist:
                        return Efficiency.Weakness;

                    case Stahl:
                        return Efficiency.CantAttack;

                    case Pflanze:
                    case Fee:
                        return Efficiency.Strength;
                }
                break;

            case Gestein:
                switch (defender){
                    case Kampf:
                    case Boden:
                    case Stahl:
                        return Efficiency.Weakness;

                    case Flug:
                    case Kaefer:
                    case Feuer:
                    case Eis:
                        return Efficiency.Strength;
                }
                break;

            case Boden:
                switch (defender){
                    case Flug:
                        return Efficiency.CantAttack;

                    case Gift:
                    case Gestein:
                    case Stahl:
                    case Feuer:
                    case Elektro:
                        return Efficiency.Strength;

                    case Kaefer:
                    case Pflanze:
                        return Efficiency.Weakness;
                }
                break;

            case Kampf:
                switch (defender){
                    case Normal:
                    case Gestein:
                    case Stahl:
                    case Eis:
                    case Unlicht:
                        return Efficiency.Strength;

                    case Flug:
                    case Gift:
                    case Kaefer:
                    case Psycho:
                    case Fee:
                        return Efficiency.Weakness;

                    case Geist:
                        return Efficiency.CantAttack;
                }
                break;

            case Eis:
                switch (defender){
                    case Flug:
                    case Boden:
                    case Pflanze:
                    case Drache:
                        return Efficiency.Strength;

                    case Stahl:
                    case Feuer:
                    case Wasser:
                    case Eis:
                        return Efficiency.Weakness;
                }
                break;
        }
        return Efficiency.Normal;
    }

    private enum Efficiency {
        Strength, Weakness, CantAttack, Normal
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public void setAttacks(ArrayList<Attack> attacks) {
        this.attacks = attacks;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if(this.hp < 0){
            this.hp = 0;
        }
    }

    public static MonsterType stringToMonsterType(String s) throws Exception {
        switch (s.toLowerCase()){
            case "psycho":
            case "psychic":
                return MonsterType.Psycho;

            case "normal":
                return MonsterType.Normal;

            case "kampf":
            case "fighting":
                return MonsterType.Kampf;

            case "flug":
            case "flying":
                return MonsterType.Flug;

            case "gift":
            case "poison":
                return MonsterType.Gift;

            case "boden":
            case "ground":
                return MonsterType.Boden;

            case "gestein":
            case "rock":
                return MonsterType.Gestein;

            case "käfer":
            case "kaefer":
            case "bug":
                return MonsterType.Kaefer;

            case "geist":
            case "ghost":
                return MonsterType.Geist;

            case "stahl":
            case "steel":
                return MonsterType.Stahl;

            case "fire":
            case "feuer":
                return MonsterType.Feuer;

            case "wasser":
            case "water":
                return MonsterType.Wasser;

            case "pflanze":
            case "grass":
                return MonsterType.Pflanze;

            case "elektro":
            case "electric":
                return MonsterType.Elektro;

            case "eis":
            case "ice":
                return MonsterType.Eis;

            case "drache":
            case "dragon":
                return MonsterType.Drache;

            case "unlicht":
            case "dark":
                return MonsterType.Unlicht;

            case "fee":
            case "fairy":
                return MonsterType.Fee;
        }
        throw new Exception("Unknown Type");
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ArrayList<MonsterType> getMonsterTypes() {
        return monsterTypes;
    }

    public void setMonsterTypes(ArrayList<MonsterType> monsterTypes) {
        this.monsterTypes = monsterTypes;
    }

    public int getDv() {
        return dv;
    }

    public void setDv(int dv) {
        this.dv = dv;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public void setBaseHp(int baseHp) {
        this.baseHp = baseHp;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Attack getA1() {
        return a1;
    }

    public void setA1(Attack a1) {
        this.a1 = a1;
    }

    public Attack getA2() {
        return a2;
    }

    public void setA2(Attack a2) {
        this.a2 = a2;
    }

    public Attack getA3() {
        return a3;
    }

    public void setA3(Attack a3) {
        this.a3 = a3;
    }

    public Attack getA4() {
        return a4;
    }

    public void setA4(Attack a4) {
        this.a4 = a4;
    }

    public String toString(){
        String msg = "";
        msg += "Name: " + getItemName() + "\nLevel: " + level + "\nRarity: " + Item.rarityToString(getItemRarity()) + "\n" + "KP: " + hp + "\nTypes: ";
        msg = addTypes(monsterTypes, msg);
        msg += "\n\n**Attacks:**\n";
        msg += "A1: \n";
        if(a1 == null){
            msg += "not selected";
        } else {
            msg += a1.toString();
        }
        msg += "\n\nA2: \n";
        if(a2 == null){
            msg += "not selected";
        } else {
            msg += a2.toString();
        }
        msg += "\n\nA3: \n";
        if(a3 == null){
            msg += "not selected";
        } else {
            msg += a3.toString();
        }
        msg += "\n\nA4: \n";
        if(a4 == null){
            msg += "not selected";
        } else {
            msg += a4.toString();
        }
        return msg;
    }

    private String addTypes(ArrayList<Monster.MonsterType> mt, String s) {
        for (Monster.MonsterType m : mt) {
            s += m.name() + " ";
        }
        return s;
    }

    public ArrayList<Attack> getAllowedAttacks(){
        ArrayList<Attack> at = new ArrayList<>();
        for (Attack a:attacks) {
            if(a.getLvl()<=level)
                at.add(a);
        }
        return at;
    }
}
