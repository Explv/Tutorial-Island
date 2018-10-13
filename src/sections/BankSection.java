package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import utils.Sleep;

import java.util.Arrays;
import java.util.List;

public final class BankSection extends TutorialSection {

    private static final Area BANK_AREA = new Area(
            new int[][]{
                    {3125, 3121},
                    {3126, 3121},
                    {3126, 3119},
                    {3118, 3119},
                    {3118, 3121},
                    {3119, 3121},
                    {3119, 3123},
                    {3115, 3123},
                    {3115, 3128},
                    {3118, 3128},
                    {3118, 3126},
                    {3122, 3126},
                    {3122, 3130},
                    {3126, 3130},
                    {3126, 3128},
                    {3128, 3128},
                    {3128, 3126},
                    {3130, 3126},
                    {3130, 3123},
                    {3125, 3123},
                    {3125, 3121}
            }
    );

    private static final List<Position> PATH_TO_BANK = Arrays.asList(
            new Position(3111, 3123, 0),
            new Position(3114, 3119, 0),
            new Position(3118, 3116, 0),
            new Position(3121, 3118, 0)
    );

    public BankSection() {
        super("Financial Advisor");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
            case 510:
                if (!BANK_AREA.contains(myPosition())) {
                    if (getWalking().walkPath(PATH_TO_BANK)) {
                        getDoorHandler().handleNextObstacle(BANK_AREA);
                    }
                } else if (getDialogues().isPendingOption()) {
                    getDialogues().selectOption("Yes.");
                } else if (getObjects().closest("Bank booth").interact("Use")) {
                    Sleep.sleepUntil(this::pendingContinue, 5000, 500);
                }
                break;
            case 520:
                if (getBank().isOpen()) {
                    getBank().close();
                } else if (!getObjects().closest("Poll booth").isVisible()) {
                    getCamera().toEntity(getObjects().closest("Poll booth"));
                } else if (getObjects().closest("Poll booth").interact("Use")) {
                    Sleep.sleepUntil(this::pendingContinue, 5000, 500);
                }
                break;
            case 525:
                if (getWidgets().closeOpenInterface() && openDoorAtPosition(new Position(3125, 3124, 0))) {
                    Sleep.sleepUntil(() -> getProgress() != 525, 5000, 500);
                }
                break;
            case 530:
                talkToInstructor();
                break;
            case 540:
                if (openDoorAtPosition(new Position(3130, 3124, 0))) {
                    Sleep.sleepUntil(() -> getProgress() != 540, 5000, 500);
                }
                break;
        }
    }

    private boolean openDoorAtPosition(final Position position) {
        RS2Object door = getObjects().closest(obj -> obj.getName().equals("Door") && obj.getPosition().equals(position));
        return door != null && door.interact("Open");
    }
}
