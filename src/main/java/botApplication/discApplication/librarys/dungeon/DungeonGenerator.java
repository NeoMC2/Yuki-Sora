package botApplication.discApplication.librarys.dungeon;

import botApplication.discApplication.librarys.dungeon.actions.*;
import botApplication.discApplication.librarys.dungeon.parts.Cave;
import core.Engine;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DungeonGenerator {

    private Engine engine;
    private Statistics statistics = new Statistics();

    private class Statistics {
        int dungeonLength;
        int dungeonCaves;

        int defaults;
        int fights;
        int bos;
        int drops;
        int xpDrops;
        int traps;
    }

    public DungeonGenerator(Engine engine) {
        this.engine = engine;
    }

    public Cave generateDungeon(Dungeon d) {
        int caves = ThreadLocalRandom.current().nextInt(5, 40);
        caves--;
        statistics.dungeonLength = caves;
        Cave dungeon = new Cave();
        dungeon.setD(d);
        dungeon.setRightDirection(true);
        dungeon.setAction(generateAction(DungeonActionType.Default));

        Cave lastCave = null;
        for (int i = 0; i < caves; i++) {
            Cave c = new Cave();
            c.setAction(generateAction(chooseActionType()));
            c.setD(d);
            c.setRightDirection(true);


            if (lastCave == null) {
                lastCave = c;
                dungeon.getJunctions().add(c);
            } else {
                lastCave.getJunctions().add(c);
                lastCave = c;
                generateJunctions(c, getJunctionCount());
            }
        }
        if (engine.getProperties().debug) {
            System.out.println("---------\n" +
                    "Generated Dungeon\n\n" +
                    "length: " + statistics.dungeonLength + "\n" +
                    "generated caves: " + statistics.dungeonCaves + "\n" +
                    "wrong way caves: " + (statistics.dungeonCaves - statistics.dungeonLength) + "\n\n" +
                    "default: " + statistics.defaults + "\n" +
                    "drops: " + statistics.drops + "\n" +
                    "xpdrops: " + statistics.xpDrops + "\n" +
                    "fights: " + statistics.fights + "\n" +
                    "traps: " + statistics.traps + "\n" +
                    "bos: " + statistics.bos + "\n" +
                    "---------");
        }
        return dungeon;
    }

    private void generateJunctions(Cave c, int left) {
        if (left > 0) {
            Cave cc = new Cave();
            cc.setD(c.getD());
            cc.setAction(generateAction(chooseActionType()));
            c.getJunctions().add(cc);
            left--;

            generateJunctions(cc, left);
            generateJunctions(cc, getJunctionCount() - 1);
        }
    }

    private int getJunctionCount() {
        int r = ThreadLocalRandom.current().nextInt(0, 100);
        int caves = 0;
        if (r > 80) {
            caves = 3;
        } else if (r > 70) {
            caves = 2;
        } else if (r > 60) {
            caves = 1;
        }
        return caves;
    }

    private DungeonActionType chooseActionType() {
        DungeonActionType ac = null;
        while (ac == null) {
            int cT = ThreadLocalRandom.current().nextInt(0, 4);
            ac = intToDungeonType(cT);
            if (ThreadLocalRandom.current().nextInt(1, 100) > dungeonTypeToRarity(ac)) {
                ac = null;
            }
        }
        return ac;
    }

    private DungeonAction generateAction(DungeonActionType t) {
        statistics.dungeonCaves++;
        DungeonAction ac = null;
        switch (t) {
            case BosFight:
                statistics.bos++;
                ac = new BosFight(engine);
                break;
            case Default:
                statistics.defaults++;
                ac = new Default(engine);
                break;
            case Drop:
                statistics.drops++;
                ac = new Drop(engine);
                break;
            case MonsterFight:
                statistics.fights++;
                ac = new MonsterFight(engine);
                break;
            case Trap:
                statistics.traps++;
                ac = new Trap(engine);
                break;
            case XPDrop:
                statistics.xpDrops++;
                ac = new XPDrop(engine);
                break;
        }
        if (ac != null)
            ac.generate();
        return ac;
    }

    private int dungeonTypeToRarity(DungeonActionType t) {
        switch (t) {
            case Default:
                return 90;
            case Drop:
                return 10;
            case MonsterFight:
                return 50;
            case Trap:
                return 30;
            case XPDrop:
                return 10;
        }
        return 100;
    }

    private int dungeonTypeToInt(DungeonActionType t) {
        switch (t) {
            case BosFight:
                return 5;
            case Default:
                return 0;
            case Drop:
                return 2;
            case MonsterFight:
                return 1;
            case Trap:
                return 4;
            case XPDrop:
                return 3;
        }
        return 0;
    }

    private DungeonActionType intToDungeonType(int i) {
        switch (i) {
            case 0:
                return DungeonActionType.Default;
            case 1:
                return DungeonActionType.MonsterFight;
            case 2:
                return DungeonActionType.Drop;
            case 3:
                return DungeonActionType.XPDrop;
            case 4:
                return DungeonActionType.Trap;
            case 5:
                return DungeonActionType.BosFight;
        }
        return DungeonActionType.Default;
    }

    private enum DungeonActionType {
        BosFight, Default, Drop, MonsterFight, Trap, XPDrop
    }
}
