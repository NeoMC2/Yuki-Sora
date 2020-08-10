package botApplication.discApplication.librarys.item.monsters;

import java.io.Serializable;

public class StatusEffect implements Serializable {

    private static final long serialVersionUID = 42L;

    private StatusEffectType type;
    private int roundsLeft;

    public enum StatusEffectType implements Serializable{
        Burn, Freeze, Paralysis, Poison, Sleep, Confusion
    }

    public static StatusEffect stringToStatEffect(String s){
        StatusEffect eff = new StatusEffect();
        switch (s.toLowerCase()){
            case "fire":
            case "burn":
                eff.setType(StatusEffectType.Burn);
                return eff;
            case "ice":
            case "freeze":
                eff.setType(StatusEffectType.Freeze);
                return eff;
            case "paralyzed":
            case "paralysis":
                eff.setType(StatusEffectType.Paralysis);
                return eff;
            case "poison":
                eff.setType(StatusEffectType.Poison);
                return eff;
            case "sleep":
                eff.setType(StatusEffectType.Sleep);
                return eff;
            case "confusion":
                eff.setType(StatusEffectType.Confusion);
                return eff;
        }
        return null;
    }

    public StatusEffectType getType() {
        return type;
    }

    public void setType(StatusEffectType type) {
        this.type = type;
    }

    public int getRoundsLeft() {
        return roundsLeft;
    }

    public void setRoundsLeft(int roundsLeft) {
        this.roundsLeft = roundsLeft;
    }
}
