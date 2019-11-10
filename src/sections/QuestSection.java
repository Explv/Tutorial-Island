package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import util.Sleep;

import java.util.Arrays;
import java.util.List;

public final class QuestSection extends TutorialSection {

    private static final Area QUEST_BUILDING = new Area(3083, 3119, 3089, 3125);

    private static final List<Position> PATH_TO_QUEST_BUILDING = Arrays.asList(
            new Position(3071, 3090, 0),
            new Position(3071, 3094, 0),
            new Position(3071, 3099, 0),
            new Position(3072, 3103, 0),
            new Position(3074, 3108, 0),
            new Position(3076, 3111, 0),
            new Position(3077, 3115, 0),
            new Position(3076, 3118, 0),
            new Position(3076, 3122, 0),
            new Position(3079, 3125, 0),
            new Position(3083, 3127, 0),
            new Position(3086, 3126, 0)
    );

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
            case 200:
                boolean isRunning = getSettings().isRunning();
                if (getSettings().setRunning(!isRunning)) {
                    Sleep.sleepUntil(() -> getSettings().isRunning() == !isRunning, 1200);
                }
                break;
            case 210:
                if (!getSettings().isRunning()) {
                    if (getSettings().setRunning(true)) {
                        Sleep.sleepUntil(() -> getSettings().isRunning(), 1200);
                    }
                } else {
                    if (getWalking().walkPath(PATH_TO_QUEST_BUILDING)) {
                        if (getDoorHandler().handleNextObstacle(QUEST_BUILDING)) {
                            Sleep.sleepUntil(() -> getProgress() != 210, 5000, 600);
                        }
                    }
                }
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
                    Sleep.sleepUntil(() -> getProgress() != 250, 5000, 600);
                }
                break;
        }
    }
}
