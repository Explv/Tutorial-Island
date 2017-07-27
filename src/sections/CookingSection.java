package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import utils.Sleep;

public class CookingSection extends TutorialSection {

    private static final Area COOK_BUILDING = new Area(3073, 3083, 3078, 3086);

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
                getWalking().webWalk(COOK_BUILDING);
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
                getWalking().webWalk(new Position(3071, 3090, 0));
                break;
        }
    }

    private void makeDough() {
        if (!"Pot of flour".equals(getInventory().getSelectedItemName())) {
            getInventory().interact("Use", "Pot of flour");
        } else if (getInventory().getItem("Bucket of water").interact()) {
            Sleep.sleepUntil(() -> getInventory().contains("Bread dough"), 3000);
        }
    }

    private void bakeDough() {
        if (!"Bread dough".equals(getInventory().getSelectedItemName())) {
            getInventory().interact("Use", "Bread dough");
        } else if (getObjects().closest("Range").interact()) {
            Sleep.sleepUntil(() -> getInventory().contains("Bread"), 5000);
        }
    }
}
