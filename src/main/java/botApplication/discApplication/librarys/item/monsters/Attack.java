package botApplication.discApplication.librarys.item.monsters;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack implements Serializable, Cloneable {

    private static final long serialVersionUID = 42L;

    private int baseDamage;
    private String attackName;
    private ArrayList<Monster.MonsterType> monsterTypes = new ArrayList<>();
    private int lvl;
    private int maxUsages;
    private int leftUses;
    private StatusEffect statusEffect;

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

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

    public int getLeftUses() {
        return leftUses;
    }

    public void setLeftUses(int leftUses) {
        this.leftUses = leftUses;
    }

    public int getMaxUsages() {
        return maxUsages;
    }

    public void setMaxUsages(int maxUsages) {
        this.maxUsages = maxUsages;
    }

    public StatusEffect getStatusEffect() {
        return statusEffect;
    }

    public void setStatusEffect(StatusEffect statusEffect) {
        this.statusEffect = statusEffect;
    }

    public String toString() {
        String s = "";
        for (Monster.MonsterType m : monsterTypes) {
            s += m.name() + " ";
        }
        String status = "";
        if (statusEffect != null)
            if (statusEffect.getType() != null)
                status = statusEffect.getType().name();

        return "Name: " + attackName + "\nLevel: " + lvl + "\nPower: " + baseDamage + "\nUsed: " + leftUses + " (" + maxUsages + ")" + "\nAttack Type: " + s + "\nStatus effect: " + status;
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
        t.setMaxUsages(maxUsages);
        t.setLeftUses(maxUsages);
        t.setStatusEffect(statusEffect);
        return t;
    }

    private ArrayList<Monster.MonsterType> cloneMonsterTypes() {
        ArrayList<Monster.MonsterType> t = new ArrayList<Monster.MonsterType>();
        monsterTypes.forEach(e -> t.add(e));
        return t;
    }

    public void use() {
        leftUses--;
    }
}
