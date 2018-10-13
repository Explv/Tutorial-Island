package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import utils.Sleep;

import java.util.Arrays;
import java.util.List;

public class CookingSection extends TutorialSection {

    private static final Area COOK_BUILDING = new Area(3073, 3083, 3078, 3086);
    private static final List<Position> PATH_TO_COOK_BUILDING = Arrays.asList(
            new Position(3087, 3091, 0),
            new Position(3083, 3086, 0),
            new Position(3080, 3083, 0)
    );

    public CookingSection() {
        super("Master Chef");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 130:
                if (getWalking().walkPath(PATH_TO_COOK_BUILDING)) {
                    if (getDoorHandler().handleNextObstacle(COOK_BUILDING)) {
                        Sleep.sleepUntil(() -> getProgress() == 140, 5000,500);
                    }
                }
                break;
            case 140:
                talkToInstructor();
                break;
            case 150:
                makeDough();
                break;
            case 160:
                bakeDough();
                break;
            case 170:
                getTabs().open(Tab.MUSIC);
                break;
            case 180:
                if (getDoorHandler().handleNextObstacle(new Position(3071, 3090, 0))) {
                    Sleep.sleepUntil(() -> getProgress() != 180, 5000,500);
                }
                break;
        }
    }

    private void makeDough() {
        if (!"Pot of flour".equals(getInventory().getSelectedItemName())) {
            getInventory().interact("Use", "Pot of flour");
        } else if (getInventory().getItem("Bucket of water").interact()) {
            Sleep.sleepUntil(() -> getInventory().contains("Bread dough"), 3000,500);
        }
    }

    private void bakeDough() {
        if (!"Bread dough".equals(getInventory().getSelectedItemName())) {
            getInventory().interact("Use", "Bread dough");
        } else if (getObjects().closest("Range").interact()) {
            Sleep.sleepUntil(() -> getInventory().contains("Bread"), 5000,500);
        }
    }
}
