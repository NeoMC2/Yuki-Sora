package botApplication.discApplication.librarys.dungeon;

import botApplication.discApplication.librarys.dungeon.actions.*;
import botApplication.discApplication.librarys.dungeon.parts.Cave;
import core.Engine;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DungeonGenerator {

    private final Engine engine;
    private final Statistics statistics = new Statistics();

    public DungeonGenerator(Engine engine) {
        this.engine = engine;
    }

    public String generateDungeonMap(Cave c) {
        ArrayList<ArrayList<Character>> map = new ArrayList<>();
        String re = "";
        int max = 0;
        addJunction(c, map, 0, 0);

        for (ArrayList<Character> m : map) {
            if (m.size() > max)
                max = m.size();
        }

        for (int j = 0; j < max; j++) {
            for (int i = 0; i < map.size(); i++) {
                if ((map.get(i).size() - 1) < j)
                    re += ' ';
                else
                    re += map.get(i).get(j);
            }
            re += "\n";
        }

        return re;
    }

    private void addJunction(Cave c, ArrayList<ArrayList<Character>> map, int x, int y) {
        if (c.getJunctions().size() == 1) {
            addCharToMap('═', map, x, y);
            addJunction(c.getJunctions().get(0), map, x + 1, y);
        } else if (c.getJunctions().size() == 2) {
            if (map.get(x).size() > y)
                moveYAxisDown(map, x, y, 3);
            addCharToMap('╗', map, x, y);
            addCharToMap('╚', map, x, y + 1);
            addCharToMap('═', map, x + 1, y);
            addCharToMap('═', map, x + 1, y + 1);
            addJunction(c.getJunctions().get(0), map, x + 1, y);
            addJunction(c.getJunctions().get(1), map, x + 1, y + 1);
        } else if (c.getJunctions().size() == 3) {
            if (map.get(x).size() > y)
                moveYAxisDown(map, x, y, 4);
            addCharToMap('╗', map, x, y);
            addCharToMap('╚', map, x, y + 1);
            addCharToMap('═', map, x + 1, y);
            addCharToMap('═', map, x + 1, y + 1);
            addCharToMap('╚', map, x, y + 2);
            addCharToMap('═', map, x + 1, y + 2);
            addJunction(c.getJunctions().get(0), map, x + 1, y);
            addJunction(c.getJunctions().get(1), map, x + 1, y + 1);
        } else if (c.getJunctions().size() == 0) {

        }
    }

    private void addCharToMap(char c, ArrayList<ArrayList<Character>> map, int x, int y) {
        for (int i = ((map.size() - 1) - x); i <= 0; i++) {
            map.add(new ArrayList<Character>());
        }

        for (int i = ((map.get(x).size() - 1) - y); i <= 0; i++) {
            map.get(x).add(' ');
        }
        map.get(x).set(y, c);
    }

    private void moveYAxisDown(ArrayList<ArrayList<Character>> map, int fromx, int fromy, int steps) {
        for (int i = 0; i < map.size(); i++) {
            ArrayList<Character> x = map.get(i);
            char last = 0;
            int s = x.size();
            if (i >= fromx)
                for (int j = 0; j < steps + s; j++) {
                    if (j >= fromy) {
                        char y = ' ';
                        if (x.size() <= j + 1) {
                            for (int k = ((x.size() - 1) - (j + 1)); k <= 0; k++) {
                                x.add(' ');
                            }
                            last = 0;
                        } else {
                            y = x.get(j);
                            last = x.get(j + 1);
                        }

                        if (last == 0)
                            x.set(j + 1, y);
                        else
                            x.set(j + 1, last);
                        if (!x.get(j).equals(' '))
                            x.set(j, '║');
                    }
                }
        }
    }

    public Cave generateDungeon(Dungeon d) {
        int caves = ThreadLocalRandom.current().nextInt(20, 70);
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
                generateJunctions(c, getJunctionCount() - 1);
            }
        }
        Cave c = new Cave();
        BosFight b = new BosFight(engine);
        b.generate();

        c.setAction(b);
        c.setD(d);
        c.setRightDirection(true);
        statistics.bos++;

        if (engine.getProperties().debug) {
            System.out.println("---------\n" +
                    "Generated Dungeon\n\n" +
                    "length: " + statistics.dungeonLength + "\n" +
                    "generated caves: " + statistics.dungeonCaves + "\n" +
                    "wrong way caves: " + (statistics.dungeonCaves - statistics.dungeonLength) + "\n\n" +
                    "1 way: " + statistics.oneWay + "\n" +
                    "2 way: " + statistics.twoWay + "\n" +
                    "3 way: " + statistics.threeWay + "\n\n" +
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

            if (c.isRightDirection())
                generateDeepCave(cc, ThreadLocalRandom.current().nextInt(2, 15));

            generateJunctions(c, left);
        }
    }

    private void generateDeepCave(Cave c, int length) {
        if (length > 0) {
            length--;
            generateJunctions(c, getJunctionCount());
            for (int i = 0; i < c.getJunctions().size(); i++) {
                if (i == 0)
                    generateDeepCave(c.getJunctions().get(i), length);
                else if (i == 1 || i == 2)
                    generateDeepCave(c.getJunctions().get(i), ThreadLocalRandom.current().nextInt(1, 3));
            }
        }
    }

    private int getJunctionCount() {
        int r = ThreadLocalRandom.current().nextInt(0, 100);
        int caves = 1;
        if (r > 80) {
            statistics.threeWay++;
            caves = 3;
        } else if (r > 70) {
            statistics.twoWay++;
            caves = 2;
        } else {
            statistics.oneWay++;
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
                return 25;
            case MonsterFight:
                return 30;
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

    private class Statistics {
        int dungeonLength;
        int dungeonCaves;

        int defaults;
        int fights;
        int bos;
        int drops;
        int xpDrops;
        int traps;

        int oneWay;
        int twoWay;
        int threeWay;
    }
}
