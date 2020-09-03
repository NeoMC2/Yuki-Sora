package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;

public interface DungeonAction {
    void action(Dungeon dungeon);

    void generate();
}
