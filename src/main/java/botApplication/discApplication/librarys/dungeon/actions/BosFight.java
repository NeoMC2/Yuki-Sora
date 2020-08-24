package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.item.Item;
import botApplication.discApplication.librarys.item.monsters.FightHandler;
import botApplication.discApplication.librarys.item.monsters.Monster;
import core.Engine;

import java.io.Serializable;

public class BosFight extends MonsterFight implements DungeonAction{

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
        if (engine.getDiscEngine().getFilesHandler() != null)
            for (Monster m : engine.getDiscEngine().getFilesHandler().getMonsters()) {
                if(m.getItemRarity() == Item.Rarity.Mystic)
                    this.m = m;
            }
    }
}
