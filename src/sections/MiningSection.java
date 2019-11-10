package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import util.Sleep;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

public final class MiningSection extends TutorialSection {

    private static final Area SMITH_AREA = new Area(3076, 9497, 3082, 9504);

    private static final List<Position> PATH_TO_SMITH_AREA = Arrays.asList(
            new Position(3080, 9518, 0),
            new Position(3080, 9511, 0),
            new Position(3080, 9505, 0)
    );

    private static final List<Position> PATH_TO_GATE = Arrays.asList(
            new Position(3086, 9505, 0),
            new Position(3091, 9503, 0)
    );

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
                if (getWalking().walkPath(PATH_TO_SMITH_AREA)) {
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
                Optional<RS2Widget> daggerWidgetOpt = getDaggerWidget();
                if (daggerWidgetOpt.isPresent()) {
                    if (daggerWidgetOpt.get().interact()) {
                        Sleep.sleepUntil(() -> getInventory().contains("Bronze dagger"), 6000, 600);
                    }
                } else {
                    smith();
                }
                break;
            case 360:
                if (getWalking().walkPath(PATH_TO_GATE)) {
                    if (getDoorHandler().handleNextObstacle(new Position(3096, 9503, 0))) {
                        Sleep.sleepUntil(() -> getProgress() != 360, 5000, 600);
                    }
                }
                break;
        }
    }

    private void smith() {
        if (!SMITH_AREA.contains(myPosition())) {
            getWalking().walk(SMITH_AREA);
        } else if (!"Bronze bar".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Bronze bar").interact("Use");
        } else if (getObjects().closest("Anvil").interact("Use")) {
            Sleep.sleepUntil(() -> getDaggerWidget().isPresent(), 5000, 600);
        }
    }

    private Optional<RS2Widget> getDaggerWidget() {
        RS2Widget daggerTextWidget = getWidgets().getWidgetContainingText(312, "Dagger");
        if (daggerTextWidget != null) {
            return Optional.ofNullable(getWidgets().get(daggerTextWidget.getRootId(), daggerTextWidget.getSecondLevelId()));
        }
        return Optional.empty();
    }

    private void smelt() {
        if (!"Tin ore".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Tin ore").interact("Use");
        } else if (getObjects().closest("Furnace").interact("Use")) {
            Sleep.sleepUntil(() -> getInventory().contains("Bronze bar"), 5000, 600);
        }
    }

    private void prospect(Rock rock) {
        RS2Object closestRock = rock.getClosestWithOre(getBot().getMethods());
        if (closestRock != null && closestRock.interact("Prospect")) {
            Sleep.sleepUntil(this::pendingContinue, 6000, 600);
        }
    }

    private void mine(Rock rock) {
        RS2Object closestRock = rock.getClosestWithOre(getBot().getMethods());
        if (closestRock != null && closestRock.interact("Mine")) {
            Sleep.sleepUntil(this::pendingContinue, 6000, 600);
        }
    }
}
