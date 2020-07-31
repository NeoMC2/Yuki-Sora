package botApplication.discApplication.librarys.item.monsters;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.item.Item;
import core.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Monster extends Item implements Serializable, Cloneable {

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
    private String evolves = "";
    private int evolveLevel = 0;

    public static MonsterType stringToMonsterType(String s) throws Exception {
        switch (s.toLowerCase()) {
            case "psycho":
            case "psychic":
                return MonsterType.psychic;

            case "normal":
                return MonsterType.normal;

            case "kampf":
            case "fighting":
                return MonsterType.fighting;

            case "flug":
            case "flying":
                return MonsterType.flying;

            case "gift":
            case "poison":
                return MonsterType.poison;

            case "boden":
            case "ground":
                return MonsterType.ground;

            case "gestein":
            case "rock":
                return MonsterType.rock;

            case "käfer":
            case "kaefer":
            case "bug":
                return MonsterType.bug;

            case "geist":
            case "ghost":
                return MonsterType.ghost;

            case "stahl":
            case "steel":
                return MonsterType.steel;

            case "fire":
            case "feuer":
                return MonsterType.fire;

            case "wasser":
            case "water":
                return MonsterType.water;

            case "pflanze":
            case "grass":
                return MonsterType.grass;

            case "elektro":
            case "electric":
                return MonsterType.electric;

            case "eis":
            case "ice":
                return MonsterType.ice;

            case "drache":
            case "dragon":
                return MonsterType.dragon;

            case "unlicht":
            case "dark":
                return MonsterType.dark;

            case "fee":
            case "fairy":
                return MonsterType.fairy;
        }
        throw new Exception("Unknown Type");
    }

    public int attack(Monster o, Attack attack, Monster enemy) {
        double dmg = (((level * (1 / 3)) + 2) + attack.getBaseDamage()) * calculateAttackEfficiency(monsterTypes, enemy.getMonsterTypes()) * calculateSTAB(o, enemy);
        enemy.setHp((int) (enemy.getHp() - dmg));
        return (int) dmg;
    }

    private double calculateSTAB(Monster o, Monster e) {
        for (MonsterType t : o.getMonsterTypes()) {
            for (MonsterType tt : e.getMonsterTypes()) {
                if (t == tt) {
                    return 1.5;
                }
            }
        }
        return 1;
    }

    public void earnXP(int xp, Engine e, DiscApplicationUser user) {
        this.xp += xp;
        isLvlUp(e, user);
    }

    private void isLvlUp(Engine e, DiscApplicationUser user) {
        int levelUpXp = level * 7 + ((dv / 5) * 10);
        if (xp >= levelUpXp) {
            this.xp -= levelUpXp;
            lvlUp(e, user);
            isLvlUp(e, user);
        }
        return;
    }

    private void lvlUp(Engine e, DiscApplicationUser user) {
        level++;
        if (!isEvolve(e, user))
            calculateHp();
    }

    public boolean isEvolve(Engine e, DiscApplicationUser user) {
        if (evolveLevel != -1)
            if (evolves != null)
                if (level != 0)
                    if (level >= evolveLevel) {
                        evolve(e, user);
                        return true;
                    }
        return false;
    }

    private void evolve(Engine e, DiscApplicationUser user) {
        for (Monster m : e.getDiscEngine().getFilesHandler().getMonsters()) {
            if (evolves.toLowerCase().equals(m.getItemName().toLowerCase())) {
                Monster mon = m.clone();
                try {
                    mon.setA1(a1);
                    mon.setA2(a2);
                    mon.setA3(a3);
                    mon.setA4(a4);
                } catch (Exception ex){
                }
                mon.getAttacks().addAll(attacks);
                mon.finish();
                user.getMonsters().add(mon);
                break;
            }
        }
        user.getMonsters().remove(this);
    }

    private void calculateHp() {
        int mah = maxHp;
        maxHp = baseHp + (level * ((((dv * level) / 100) + 10) / (((dv) / 100) + 10)));
        hp += maxHp - mah;
    }

    public double calculateAttackEfficiency(ArrayList<MonsterType> typesAttack, ArrayList<MonsterType> typesDefend) {
        int strength = 0;
        for (MonsterType own : typesAttack) {
            for (MonsterType enemy : typesDefend) {
                switch (getAttackEfficiency(own, enemy)) {

                    case Strength:
                        strength++;
                        break;
                    case Weakness:
                        strength--;
                        break;
                    case CantAttack:
                        strength = -2;
                        break;
                }
            }
        }
        if (strength <= -2) {
            return 0;
        } else if (strength <= -1) {
            return 0.5;
        } else if (strength >= 1) {
            return 2;
        } else {
            return 1;
        }
    }

    public Efficiency getAttackEfficiency(MonsterType attacker, MonsterType defender) {
        switch (attacker) {

            case psychic:
                switch (defender) {
                    case fighting:
                    case poison:
                        return Efficiency.Strength;

                    case steel:
                    case psychic:
                        return Efficiency.Weakness;

                    case dark:
                        return Efficiency.CantAttack;
                }
                break;

            case ghost:
                switch (defender) {
                    case normal:
                        return Efficiency.CantAttack;

                    case ghost:
                    case psychic:
                        return Efficiency.Strength;

                    case dark:
                        return Efficiency.Weakness;
                }
                break;

            case dark:
                switch (defender) {
                    case fighting:
                    case dark:
                    case fairy:
                        return Efficiency.Weakness;

                    case ghost:
                    case psychic:
                        return Efficiency.Strength;
                }
                break;

            case dragon:
                switch (defender) {
                    case steel:
                        return Efficiency.Weakness;

                    case dragon:
                        return Efficiency.Strength;

                    case fairy:
                        return Efficiency.CantAttack;
                }
                break;

            case steel:
                switch (defender) {
                    case steel:
                    case fire:
                    case water:
                    case electric:
                        return Efficiency.Weakness;

                    case ice:
                    case rock:
                    case fairy:
                        return Efficiency.Strength;
                }
                break;

            case fairy:
                switch (defender) {
                    case fighting:
                    case dragon:
                    case dark:
                        return Efficiency.Strength;

                    case poison:
                    case steel:
                    case fire:
                        return Efficiency.Weakness;
                }
                break;

            case normal:
                switch (defender) {
                    case rock:
                    case steel:
                        return Efficiency.Weakness;

                    case ghost:
                        return Efficiency.CantAttack;
                }
                break;

            case fire:
                switch (defender) {
                    case rock:
                    case fire:
                    case water:
                    case dragon:
                        return Efficiency.Weakness;

                    case bug:
                    case steel:
                    case grass:
                    case ice:
                        return Efficiency.Strength;
                }
                break;

            case water:
                switch (defender) {
                    case ground:
                    case rock:
                    case fire:
                        return Efficiency.Strength;

                    case water:
                    case grass:
                    case dragon:
                        return Efficiency.Weakness;
                }
                break;

            case electric:
                switch (defender) {
                    case flying:
                    case water:
                        return Efficiency.Strength;

                    case ground:
                        return Efficiency.CantAttack;

                    case grass:
                    case electric:
                    case dragon:
                        return Efficiency.Weakness;
                }
                break;

            case grass:
                switch (defender) {
                    case flying:
                    case poison:
                    case bug:
                    case steel:
                    case fire:
                    case grass:
                    case dragon:
                        return Efficiency.Weakness;

                    case ground:
                    case rock:
                    case water:
                        return Efficiency.Strength;
                }
                break;

            case flying:
                switch (defender) {
                    case fighting:
                    case bug:
                    case grass:
                        return Efficiency.Strength;

                    case rock:
                    case steel:
                    case electric:
                        return Efficiency.Weakness;
                }
                break;

            case bug:
                switch (defender) {
                    case fighting:
                    case flying:
                    case poison:
                    case ghost:
                    case steel:
                    case fire:
                    case fairy:
                        return Efficiency.Weakness;

                    case grass:
                    case psychic:
                    case dark:
                        return Efficiency.Strength;
                }
                break;

            case poison:
                switch (defender) {
                    case poison:
                    case ground:
                    case rock:
                    case ghost:
                        return Efficiency.Weakness;

                    case steel:
                        return Efficiency.CantAttack;

                    case grass:
                    case fairy:
                        return Efficiency.Strength;
                }
                break;

            case rock:
                switch (defender) {
                    case fighting:
                    case ground:
                    case steel:
                        return Efficiency.Weakness;

                    case flying:
                    case bug:
                    case fire:
                    case ice:
                        return Efficiency.Strength;
                }
                break;

            case ground:
                switch (defender) {
                    case flying:
                        return Efficiency.CantAttack;

                    case poison:
                    case rock:
                    case steel:
                    case fire:
                    case electric:
                        return Efficiency.Strength;

                    case bug:
                    case grass:
                        return Efficiency.Weakness;
                }
                break;

            case fighting:
                switch (defender) {
                    case normal:
                    case rock:
                    case steel:
                    case ice:
                    case dark:
                        return Efficiency.Strength;

                    case flying:
                    case poison:
                    case bug:
                    case psychic:
                    case fairy:
                        return Efficiency.Weakness;

                    case ghost:
                        return Efficiency.CantAttack;
                }
                break;

            case ice:
                switch (defender) {
                    case flying:
                    case ground:
                    case grass:
                    case dragon:
                        return Efficiency.Strength;

                    case steel:
                    case fire:
                    case water:
                    case ice:
                        return Efficiency.Weakness;
                }
                break;
        }
        return Efficiency.Normal;
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
        if (this.hp < 0) {
            this.hp = 0;
        }
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

    public String getEvolves() {
        return evolves;
    }

    public void setEvolves(String evolves) {
        this.evolves = evolves;
    }

    public int getEvolveLevel() {
        return evolveLevel;
    }

    public void setEvolveLevel(int evolveLevel) {
        this.evolveLevel = evolveLevel;
    }

    public String toString() {
        String msg = "";
        msg += "Name: " + getItemName() + "\nLevel: " + level + "\nRarity: " + Item.rarityToString(getItemRarity()) + "\n" + "HP: " + hp + " (" + maxHp + ")" + "\nTypes: ";
        msg = addTypes(monsterTypes, msg);
        msg += "\n\n**Attacks:**\n";
        msg += "A1: \n";
        if (a1 == null) {
            msg += "not selected";
        } else {
            msg += a1.toString();
        }
        msg += "\n\nA2: \n";
        if (a2 == null) {
            msg += "not selected";
        } else {
            msg += a2.toString();
        }
        msg += "\n\nA3: \n";
        if (a3 == null) {
            msg += "not selected";
        } else {
            msg += a3.toString();
        }
        msg += "\n\nA4: \n";
        if (a4 == null) {
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

    public ArrayList<Attack> getAllowedAttacks() {
        ArrayList<Attack> at = new ArrayList<>();
        for (Attack a : attacks) {
            if (a.getLvl() <= level)
                at.add(a);
        }
        return at;
    }

    public void finish() {
        int c = 1;
        for (Attack at : attacks) {
            if (at.getLvl() <= level)
                if (c == 1) {
                    a1 = at;
                    c++;
                } else if (c == 2) {
                    a2 = at;
                    c++;
                } else if (c == 3) {
                    a3 = at;
                    c++;
                } else if (c == 4) {
                    a4 = at;
                    c++;
                } else {
                    break;
                }
        }
        if (evolves != null)
            if (evolves.equals("")){
                evolves = null;
                evolveLevel = -1;
            }
    }

    public Monster clone() {
        Monster t = new Monster();

        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        /*
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
         */
        t.setHp(hp);
        t.setBaseHp(baseHp);
        t.setLevel(level);
        t.setXp(0);
        t.setMonsterTypes(cloneMonsterTypes());
        t.setAttacks(cloneAttacks());
        try {
            t.setEvolves(evolves);
            t.setEvolveLevel(evolveLevel);
        } catch (Exception e){

        }
        t.finish();
        return t;
    }

    private ArrayList<Attack> cloneAttacks() {
        ArrayList<Attack> t = new ArrayList<>();
        attacks.forEach(e -> t.add(e.clone()));
        return t;
    }

    private ArrayList<MonsterType> cloneMonsterTypes() {
        ArrayList<MonsterType> t = new ArrayList<>();
        monsterTypes.forEach(e -> t.add(e));
        return t;
    }

    public enum MonsterType {
        psychic, ghost, dark, dragon, steel, fairy, normal, fire, water, electric, grass, flying, bug, poison, rock, ground, fighting, ice, Psycho, Geist, Unlicht, Drache, Stahl, Fee, Normal, Feuer, Wasser, Elektro, Pflanze, Flug, Kaefer, Gift, Gestein, Boden, Kampf, Eis
    }

    private enum Efficiency {
        Strength, Weakness, CantAttack, Normal
    }
}
