package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.librarys.item.monsters.Monster;
import core.Engine;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class MonsterFight implements DungeonAction, Serializable {

    private Difficulty dif;
    private Monster m;
    public Engine engine;
    public Dungeon d;
    private String msg = "Seems like here is a monster!";

    public MonsterFight(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        m = m.clone();
        dungeon.getTextChannel().sendMessage(msg).queue();
        d = dungeon;
        FightHandler h = new FightHandler(engine, dungeon.getTextChannel(), dungeon.getG());
        engine.getDiscEngine().getFightHandlers().add(h);
        h.beginAi(dungeon.getM(), m, dungeon.getMember(), this);
    }

    @Override
    public void generate() {
        int i = ThreadLocalRandom.current().nextInt(1, 100);
        if (dif == null)
            if (i < 40)
                dif = Difficulty.Hard;
            else if (i < 60)
                dif = Difficulty.Normal;
            else
                dif = Difficulty.Easy;
        if (engine != null)
            if (engine.getDiscEngine().getFilesHandler() != null)
                for (Monster m : engine.getDiscEngine().getFilesHandler().getMonsters()) {
                    if (dif == Difficulty.Easy) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 0) {
                            this.m = m;
                            break;
                        }
                    }

                    if (dif == Difficulty.Normal) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 1) {
                            this.m = m;
                            break;
                        }

                    }

                    if (dif == Difficulty.Hard) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 2) {
                            this.m = m;
                            break;
                        }
                    }
                    if (this.m == null) {
                        this.m = engine.getDiscEngine().getFilesHandler().getMonsters().get(ThreadLocalRandom.current().nextInt(0, engine.getDiscEngine().getFilesHandler().getMonsters().size() - 1));
                    }
                }
    }

    public void setDif(Difficulty dif) {
        this.dif = dif;
    }

    public void fightOver(boolean won) {
        d.caveActionFinished(!won);
    }

    public enum Difficulty {
        Easy, Normal, Hard
    }
}
