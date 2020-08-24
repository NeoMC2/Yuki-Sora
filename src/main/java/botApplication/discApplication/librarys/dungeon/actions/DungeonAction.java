package botApplication.discApplication.librarys.dungeon.actions;

import botApplication.discApplication.librarys.dungeon.Dungeon;

public interface DungeonAction {
    public void action(Dungeon dungeon);

    public void generate();
}
