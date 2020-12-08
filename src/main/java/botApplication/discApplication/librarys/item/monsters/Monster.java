package botApplication.discApplication.librarys.item.monsters;

import botApplication.discApplication.librarys.DiscApplicationUser;
import core.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Monster implements Serializable, Cloneable {

    private static final long serialVersionUID = 42L;

    private ArrayList<Attack> attacks = new ArrayList<>();
    private ArrayList<StatusEffect> statusEffects = new ArrayList<>();
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
    private ArrayList<String> evolves = new ArrayList<>();
    private MonsterType evolveDirection;
    private int evolveLevel = 0;
    private boolean shown = true;

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

    public static MonsterType getRandomMonsterType() {
        int r = ThreadLocalRandom.current().nextInt(0, 17);
        switch (r) {
            case 0:
                return MonsterType.psychic;

            case 1:
                return MonsterType.ghost;

            case 2:
                return MonsterType.ice;

            case 3:
                return MonsterType.dark;

            case 4:
                return MonsterType.dragon;

            case 5:
                return MonsterType.steel;

            case 6:
                return MonsterType.fairy;

            case 7:
                return MonsterType.normal;

            case 8:
                return MonsterType.fire;

            case 9:
                return MonsterType.water;

            case 10:
                return MonsterType.electric;

            case 11:
                return MonsterType.grass;

            case 12:
                return MonsterType.flying;

            case 13:
                return MonsterType.bug;

            case 14:
                return MonsterType.poison;

            case 15:
                return MonsterType.rock;

            case 16:
                return MonsterType.ground;

            case 17:
                return MonsterType.fighting;
        }
        return MonsterType.Normal;
    }

    public int calculateStateDmg(boolean turn) {
        int allDmg = 0;
        if (statusEffects == null)
            statusEffects = new ArrayList<>();
        Iterator<StatusEffect> itr = statusEffects.iterator();
        while (itr.hasNext()) {
            StatusEffect ss = itr.next();
            if (!turn)
                if (ss.getType() == StatusEffect.StatusEffectType.Freeze) {
                    if (ThreadLocalRandom.current().nextInt(0, 100) < 20) {
                        itr.remove();
                        continue;
                    }
                }
            if (!turn)
                if (ss.getType() == StatusEffect.StatusEffectType.Sleep || ss.getType() == StatusEffect.StatusEffectType.Confusion)
                    if (ss.getRoundsLeft() <= 0) {
                        itr.remove();
                        continue;
                    }
            if (!turn)
                if (ss.getType() == StatusEffect.StatusEffectType.Burn || ss.getType() == StatusEffect.StatusEffectType.Poison) {
                    int dmg = maxHp / 16;
                    hp -= dmg;
                    allDmg += dmg;
                }
            if (turn)
                if (ss.getType() != StatusEffect.StatusEffectType.Freeze && ss.getType() != StatusEffect.StatusEffectType.Sleep && ss.getType() != StatusEffect.StatusEffectType.Paralysis)
                    if (ss.getType() == StatusEffect.StatusEffectType.Confusion) {
                        double dmg = (((level * (1 / 3)) + 2) + 40);
                        hp -= dmg;
                        allDmg += dmg;
                    }
            ss.setRoundsLeft(ss.getRoundsLeft() - 1);
        }
        return allDmg;
    }

    public int attack(Monster o, Attack attack, Monster enemy, Engine engine) {
        attack.use();
        //game is fck'n unbalanced...so we do this......
        int baseDmg = attack.getBaseDamage() / 3;
        double attackEfficiency = calculateAttackEfficiency(monsterTypes, enemy.getMonsterTypes());
        double stab = calculateSTAB(o, enemy);
        double lvl = level;
        double dmg = (((lvl * (1 / 3)) + 2) + baseDmg) * attackEfficiency * stab;
        //BIG DEBUG O.O
        engine.getUtilityBase().printOutput("[attack damage calculation] monster attacker: " + o.toString() + " \nenemy: " + enemy.toString() + " \nattack: " + attack.toString() + " \nbase dmg: " + baseDmg + " efficiency: " + attackEfficiency + " stab: " + stab + " dmg: " + dmg, true);
        enemy.setHp((int) (enemy.getHp() - dmg));
        if (attack.getStatusEffect() != null) {
            StatusEffect e = new StatusEffect();
            e.setType(attack.getStatusEffect().getType());
            if (attack.getStatusEffect().getType() == StatusEffect.StatusEffectType.Sleep || attack.getStatusEffect().getType() == StatusEffect.StatusEffectType.Confusion)
                e.setRoundsLeft(ThreadLocalRandom.current().nextInt(1, 7));

            if (enemy.getStatusEffects() != null) {
                enemy.setStatusEffects(new ArrayList<>());
            } else {
                if (enemy.getStatusEffects() == null)
                    enemy.setStatusEffects(new ArrayList<>());
                Iterator<StatusEffect> it = enemy.getStatusEffects().iterator();
                while (it.hasNext()) {
                    StatusEffect statusEffect = it.next();
                    if (attack.getStatusEffect().getType() == statusEffect.getType())
                        enemy.getStatusEffects().remove(statusEffect);
                }
                enemy.getStatusEffects().add(e);
            }
        }
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

    public int getLevelUpXp() {
        return level * 7 + ((dv / 5) * 10);
    }

    private void isLvlUp(Engine e, DiscApplicationUser user) {
        int levelUpXp = getLevelUpXp();
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
        if (user == null) {
            return;
        }
        if (evolveDirection != null) {
            for (String s : evolves) {
                for (Monster m : e.getDiscEngine().getFilesHandler().getMonsters()) {
                    if (m.getItemName().toLowerCase().equals(s.toLowerCase())) {
                        for (MonsterType t : m.getMonsterTypes()) {
                            if (t == evolveDirection) {
                                buildThaMonster(m, user);
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            String evolvesIn = "";
            if (evolves.size() - 1 > 1)
                evolvesIn = evolves.get(ThreadLocalRandom.current().nextInt(0, evolves.size() - 1));
            else
                evolves.get(0);
            for (Monster m : e.getDiscEngine().getFilesHandler().getMonsters()) {
                if (evolvesIn.toLowerCase().equals(m.getItemName().toLowerCase())) {
                    buildThaMonster(m, user);
                }
            }
        }
        user.getMonsters().remove(this);
    }

    private void buildThaMonster(Monster m, DiscApplicationUser user) {
        Monster mon = m.clone();
        try {
            mon.setA1(a1);
            mon.setA2(a2);
            mon.setA3(a3);
            mon.setA4(a4);
        } catch (Exception ex) {
        }
        mon.getAttacks().addAll(attacks);
        mon.finish();
        user.getMonsters().add(mon);
    }

    private void calculateHp() {
        maxHp = baseHp + (level * ((((dv * level) / 100) + 10) / (((dv) / 100) + 10)));
        hp = maxHp;
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

    public ArrayList<String> getEvolves() {
        return evolves;
    }

    public void setEvolves(ArrayList<String> evolves) {
        this.evolves = evolves;
    }

    public int getEvolveLevel() {
        return evolveLevel;
    }

    public void setEvolveLevel(int evolveLevel) {
        this.evolveLevel = evolveLevel;
    }

    public ArrayList<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public void setStatusEffects(ArrayList<StatusEffect> statusEffects) {
        this.statusEffects = statusEffects;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public MonsterType getEvolveDirection() {
        return evolveDirection;
    }

    public void setEvolveDirection(MonsterType evolveDirection) {
        this.evolveDirection = evolveDirection;
    }

    public String toString() {
        String msg = "";
        String g = addStatusEffects();
        msg += "Name: " + getItemName() + "\nLevel: " + level + "\nXP: " + xp + " (" + getLevelUpXp() + ")" + "\nRarity: " + Item.rarityToString(getItemRarity()) + "\n" + "HP: " + hp + " (" + maxHp + ")" + "\nStaus Effects: " + g + "\nTypes: ";
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

    private String addStatusEffects() {
        String s = "";
        if (statusEffects != null)
            for (StatusEffect eff : statusEffects) {
                s += eff.getType().name() + ", ";
            }
        return s;
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
            if (evolves.equals("")) {
                evolves = null;
                evolveLevel = -1;
            }
        calculateHp();
    }

    public Monster clone() {
        Monster t = new Monster();

        t.setImgUrl(getImgUrl());
        t.setItemName(getItemName());
        t.setItemRarity(getItemRarity());
        t.setHp(hp);
        t.setBaseHp(baseHp);
        t.setLevel(level);
        t.setXp(0);
        t.setMonsterTypes(cloneMonsterTypes());
        t.setAttacks(cloneAttacks());
        try {
            t.setEvolves(cloneEv());
            t.setEvolveLevel(evolveLevel);
        } catch (Exception e) {

        }
        t.finish();
        return t;
    }

    private ArrayList<String> cloneEv() {
        ArrayList<String> ec = new ArrayList<>();
        evolves.forEach(e -> ec.add(e));
        return ec;
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

    public void feed(Food f) {
        a1.setLeftUses(a1.getMaxUsages());
        a2.setLeftUses(a2.getMaxUsages());
        a3.setLeftUses(a3.getMaxUsages());
        a4.setLeftUses(a4.getMaxUsages());

        if (f.getType() != null)
            setEvolveDirection(f.getType());
    }

    public enum MonsterType {
        psychic, ghost, dark, dragon, steel, fairy, normal, fire, water, electric, grass, flying, bug, poison, rock, ground, fighting, ice, Psycho, Geist, Unlicht, Drache, Stahl, Fee, Normal, Feuer, Wasser, Elektro, Pflanze, Flug, Kaefer, Gift, Gestein, Boden, Kampf, Eis
    }

    private enum Efficiency {
        Strength, Weakness, CantAttack, Normal
    }
}