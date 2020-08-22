package botApplication.discApplication.librarys.dungeon.parts;

import botApplication.discApplication.librarys.DiscApplicationUser;
import botApplication.discApplication.librarys.dungeon.Dungeon;
import botApplication.discApplication.librarys.dungeon.actions.DungeonAction;

import java.io.Serializable;
import java.util.ArrayList;

public class Cave implements Serializable {

    private ArrayList<Cave> junctions = new ArrayList<>();
    private DungeonAction action;
    private transient Dungeon d;
    private transient Cave goneBefore;
    private boolean rightDirection = false;
    private transient boolean actionPerformed = false;

    public void enter(Cave before) {
        if (before != null)
            goneBefore = before;
        if (!actionPerformed) {
            actionPerformed = true;
            action.action(d);
        } else {
            d.caveActionFinished(false);
        }
    }

    public int junctions() {
        return junctions.size();
    }

    public Cave goFurther(int i) {
        return junctions.get(i - 1);
    }

    public void init(Dungeon d) {
        this.d = d;
    }

    public ArrayList<Cave> getJunctions() {
        return junctions;
    }

    public void setJunctions(ArrayList<Cave> junctions) {
        this.junctions = junctions;
    }

    public DungeonAction getAction() {
        return action;
    }

    public void setAction(DungeonAction action) {
        this.action = action;
    }

    public Dungeon getD() {
        return d;
    }

    public void setD(Dungeon d) {
        this.d = d;
    }

    public boolean isRightDirection() {
        return rightDirection;
    }

    public void setRightDirection(boolean rightDirection) {
        this.rightDirection = rightDirection;
    }

    public Cave getGoneBefore() {
        return goneBefore;
    }

    public void setGoneBefore(Cave goneBefore) {
        this.goneBefore = goneBefore;
    }
}
