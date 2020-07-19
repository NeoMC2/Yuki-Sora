package botApplication.discApplication.librarys.transaktion.monsters;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.ArrayList;

public class Attack implements Serializable {

    private static final long serialVersionUID = 42L;

    private int baseDamage;
    //private AttackType attackType;
    private String attackName;
    private ArrayList<Monster.MonsterType> monsterTypes = new ArrayList<>();

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
}
