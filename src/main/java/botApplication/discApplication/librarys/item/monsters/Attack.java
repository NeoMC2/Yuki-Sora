package botApplication.discApplication.librarys.item.monsters;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack implements Serializable, Cloneable {

    private static final long serialVersionUID = 42L;

    private int baseDamage;
    //private AttackType attackType;
    private String attackName;
    private ArrayList<Monster.MonsterType> monsterTypes = new ArrayList<>();
    private int lvl;
    private int usage;
    private int used;

    //public enum AttackType {
    //   Heal, Punch
    //}

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    //public AttackType getAttackType() {
    //return attackType;
    //}

    //public void setAttackType(AttackType attackType) {
    //this.attackType = attackType;
    //}

    public String getAttackName() {
        return attackName;
    }

    public void setAttackName(String attackName) {
        this.attackName = attackName;
    }

    public ArrayList<Monster.MonsterType> getMonsterTypes() {
        return monsterTypes;
    }

    public void setMonsterTypes(ArrayList<Monster.MonsterType> monsterTypes) {
        this.monsterTypes = monsterTypes;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public String toString() {
        String s = "";
        for (Monster.MonsterType m : monsterTypes) {
            s += m.name() + " ";
        }
        return "Name: " + attackName + "\nUsed: " + used + " (" + usage + ")" + "\nLevel: " + lvl + "\nPower: " + baseDamage + "\nAttack Type: " + s;
    }

    public Attack clone() {
        Attack t = new Attack();
        /*
    private int baseDamage;
    //private AttackType attackType;
    private String attackName;
    private ArrayList<Monster.MonsterType> monsterTypes = new ArrayList<>();
    private int lvl;
         */
        t.setBaseDamage(baseDamage);
        t.setAttackName(attackName);
        t.setLvl(lvl);
        t.setMonsterTypes(cloneMonsterTypes());
        t.setUsage(usage);
        t.setUsed(usage);
        return t;
    }

    private ArrayList<Monster.MonsterType> cloneMonsterTypes() {
        ArrayList<Monster.MonsterType> t = new ArrayList<Monster.MonsterType>();
        monsterTypes.forEach(e -> t.add(e));
        return t;
    }

    public void use(){
        used++;
    }
}
