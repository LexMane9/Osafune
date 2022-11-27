package elements;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class DropTargetActor extends BaseActor {
    private boolean targetable;

    public DropTargetActor(float x, float y, Stage s, float w, float h) {
        super(x, y, s);
        targetable = true;
    }

    /**
     * Check if this actor can be targeted by a DragAndDrop actor.
     *
     * @return can this actor be targeted
     */
    public boolean isTargetable() {
        return targetable;
    }

    /**
     * Set whether this actor can be targeted by a DragAndDrop actor.
     *
     * @param t can this actor be targeted
     */
    public void setTargetable(boolean t) {
        targetable = t;
    }
}