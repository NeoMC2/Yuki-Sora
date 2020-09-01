package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.librarys.item.monsters.Monster;
import core.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BosFight extends MonsterFight implements DungeonAction{

    private Difficulty dif;
    private String msg = "Seems like this is a bosfight!";
    private Monster m;

    public BosFight(Engine engine) {
        super(engine);
    }

    @Override
    public void action(Dungeon dungeon) {
        this.d = dungeon;
        dungeon.getTextChannel().sendMessage(msg).queue();
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

        ArrayList<Monster> mnster = new ArrayList<>();
        if (engine != null)
            if (engine.getDiscEngine().getFilesHandler() != null)
                for (Monster m : engine.getDiscEngine().getFilesHandler().getMonsters()) {
                    if (dif == Difficulty.Easy) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 0) {
                            if (m.isShown()) {
                                m.setLevel(ThreadLocalRandom.current().nextInt(20, 30));
                                mnster.add(m);
                            }
                        }
                    }

                    if (dif == Difficulty.Normal) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 1) {
                            if (m.isShown()) {
                                m.setLevel(ThreadLocalRandom.current().nextInt(25, 40));
                                mnster.add(m);
                            }
                        }

                    }

                    if (dif == Difficulty.Hard) {
                        if (Item.rarityToInt(m.getItemRarity()) <= 2) {
                            if (m.isShown()){
                                m.setLevel(ThreadLocalRandom.current().nextInt(35, 60));
                                mnster.add(m);
                            }
                        }
                    }

                    try {
                        this.m = mnster.get(ThreadLocalRandom.current().nextInt(0, mnster.size() - 1));
                    } catch (Exception e) {
                    }

                    if (this.m == null) {
                        this.m = engine.getDiscEngine().getFilesHandler().getMonsters().get(ThreadLocalRandom.current().nextInt(0, engine.getDiscEngine().getFilesHandler().getMonsters().size() - 1));
                    }
                }
    }

    public enum Difficulty {
        Easy, Normal, Hard
    }
}
