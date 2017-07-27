package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Tab;
import utils.CachedWidget;
import utils.Sleep;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public final class QuestSection extends TutorialSection {

    private static final Area QUEST_BUILDING = new Area(3083, 3119, 3089, 3125);

    public QuestSection() {
        super("Quest Guide");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 183:
                getTabs().open(Tab.EMOTES);
                break;
            case 187:
                // Use random emote
                if (getWidgets().get(216, 1, new Random().nextInt(20)).interact()) {
                    Sleep.sleepUntil(() -> getProgress() != 187, 5000);
                }
                break;
            case 190:
                getTabs().open(Tab.SETTINGS);
                break;
            case 200:
                getSettings().setRunning(true);
                break;
            case 210:
                getWalking().webWalk(QUEST_BUILDING);
                break;
            case 220:
                talkToInstructor();
                break;
            case 230:
                getTabs().open(Tab.QUEST);
                break;
            case 240:
                talkToInstructor();
                break;
            case 250:
                if (getObjects().closest("Ladder").interact("Climb-down")) {
                    Sleep.sleepUntil(() -> getProgress() != 250, 5000);
                }
                break;
        }
    }
}
