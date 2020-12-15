package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;
import core.Engine;
import org.json.simple.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

public class Trap implements DungeonAction {

    private final Engine engine;

    private int dmg = 0;

    public Trap(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void action(Dungeon dungeon) {
        JSONObject req = engine.getDiscEngine().getApiManager().getUserMonsterByIds(dungeon.getMember().getId(), dungeon.getM());
        if((Long)req.get("status") != 200){
            dungeon.getTextChannel().sendMessage("Api respond error! Dungeon quit!").queue();
            dungeon.caveActionFinished(true);
            return;
        }
        JSONObject mns = (JSONObject) req.get("data");
        dmg = ThreadLocalRandom.current().nextInt(3, 20);
        dmg = (int) ((dmg * ((Long) mns.get("dv")/ 50 + 1)) / 2);
        long rhp = (Long) mns.get("hp");
        rhp -= dmg;
        if (rhp <= 0)
            rhp = 1;
        mns.put("hp", rhp - dmg);
        JSONObject r = engine.getDiscEngine().getApiManager().dmgOnMonster(dungeon.getMember().getId(), dungeon.getM(), dmg);
        dungeon.getTextChannel().sendMessage("Seems like this is a trap! Your monster got " + dmg + " damage and has " + ((Long) r.get("hp")) + " hp left!").queue();
        dungeon.caveActionFinished(false);
    }

    @Override
    public void generate() {
    }
}