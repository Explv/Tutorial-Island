package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import utils.Sleep;

import java.util.Optional;

public final class MiningSection extends TutorialSection {

    private static final Area SMITH_AREA = new Area(3076, 9497, 3082, 9504);

    public MiningSection() {
        super("Mining Instructor");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 260:
                if (!isInstructorVisible()) {
                    walkToInstructor();
                } else {
                    talkToInstructor();
                }
                break;
            case 270:
                prospect(Rock.TIN);
                break;
            case 280:
                prospect(Rock.COPPER);
                break;
            case 290:
                talkToInstructor();
                break;
            case 300:
                mine(Rock.TIN);
                break;
            case 310:
                mine(Rock.COPPER);
                break;
            case 320:
                if (getTabs().open(Tab.INVENTORY)) {
                    smelt();
                }
                break;
            case 330:
                talkToInstructor();
                break;
            case 340:
                if (getTabs().open(Tab.INVENTORY)) {
                    smith();
                }
                break;
            case 350:
                getDaggerWidget().ifPresent(widget -> {
                    if (widget.interact()) {
                        Sleep.sleepUntil(() -> getInventory().contains("Bronze dagger"), 6000);
                    }
                });
                break;
            case 360:
                getWalking().webWalk(new Position(3109, 9510, 0));
                break;
        }
    }

    private void walkToInstructor() {
        WebWalkEvent webWalkEvent = new WebWalkEvent(SMITH_AREA);
        webWalkEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return isInstructorVisible();
            }
        });
        execute(webWalkEvent);
    }

    private void smith() {
        if (!SMITH_AREA.contains(myPosition())) {
            getWalking().walk(SMITH_AREA.getRandomPosition());
        } else if (!"Bronze bar".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Bronze bar").interact("Use");
        } else if (getObjects().closest("Anvil").interact("Use")) {
            Sleep.sleepUntil(() -> getDaggerWidget().isPresent(), 5000);
        }
    }

    private Optional<RS2Widget> getDaggerWidget() {
        RS2Widget daggerTextWidget = getWidgets().getWidgetContainingText("Dagger");
        if (daggerTextWidget != null) {
            return Optional.ofNullable(getWidgets().get(daggerTextWidget.getRootId(), daggerTextWidget.getSecondLevelId()));
        }
        return Optional.empty();
    }

    private void smelt() {
        if (!"Tin ore".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Tin ore").interact("Use");
        } else if (getObjects().closest("Furnace").interact("Use")) {
            Sleep.sleepUntil(() -> getInventory().contains("Bronze bar"), 5000);
        }
    }

    private void prospect(Rock rock) {
        RS2Object closestRock = rock.getClosestWithOre(getBot().getMethods());
        if (closestRock != null && closestRock.interact("Prospect")) {
            Sleep.sleepUntil(this::pendingContinue, 6000);
        }
    }

    private void mine(Rock rock) {
        RS2Object closestRock = rock.getClosestWithOre(getBot().getMethods());
        if (closestRock != null && closestRock.interact("Mine")) {
            Sleep.sleepUntil(this::pendingContinue, 6000);
        }
    }
}

enum Rock {

    COPPER((short) 4645, (short) 4510),
    TIN((short) 53);

    private final short[] COLOURS;

    Rock(final short... COLOURS) {
        this.COLOURS = COLOURS;
    }

    public RS2Object getClosestWithOre(final MethodProvider S) {
        //noinspection unchecked
        return S.getObjects().closest(obj -> {
            short[] colours = obj.getDefinition().getModifiedModelColors();
            if (colours != null) {
                for (short c : colours) {
                    for (short col : COLOURS) {
                        if (c == col) return true;
                    }
                }
            }
            return false;
        });
    }
}
