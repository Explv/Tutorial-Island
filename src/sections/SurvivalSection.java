package sections;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.GroundDecoration;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WalkingEvent;
import util.Sleep;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class SurvivalSection extends TutorialSection {

    private final List<Position> PATH_TO_GATE = Arrays.asList(
            new Position(3098, 3092, 0),
            new Position(3092, 3091, 0)
    );

    public SurvivalSection() {
        super("Survival Expert");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }
        switch (getProgress()) {
            case 20:
                talkToInstructor();
                break;
            case 30:
                getTabs().open(Tab.INVENTORY);
                break;
            case 40:
                fish();
                break;
            case 50:
                getTabs().open(Tab.SKILLS);
                break;
            case 60:
                talkToInstructor();
                break;
            case 70:
                chopTree();
                break;
            case 80:
            case 90:
            case 100:
            case 110:
                if (getTabs().getOpen() != Tab.INVENTORY) {
                    getTabs().open(Tab.INVENTORY);
                } else if (!getInventory().contains("Raw shrimps")) {
                    fish();
                } else if (getObjects().closest("Fire") == null || getWidgets().getWidgetContainingText("time to light a fire") != null) {
                    if (!getInventory().contains("Logs")) {
                        chopTree();
                    } else {
                        lightFire();
                    }
                } else {
                    cook();
                }
                break;
            case 120:
                RS2Object gate = getObjects().closest("Gate");
                if (gate != null && gate.isVisible()) {
                    if (gate.interact("Open")) {
                        Sleep.sleepUntil(() -> getProgress() == 130, 5000, 600);
                    }
                } else {
                    getWalking().walkPath(PATH_TO_GATE);
                }
                break;
        }
    }

    private void chopTree() {
        Entity tree = getObjects().closest("Tree");
        if (tree != null && tree.interact("Chop down")) {
            Sleep.sleepUntil(() -> getInventory().contains("Logs") || !tree.exists(), 10_000, 600);
        }
    }

    private void fish() {
        NPC fishingSpot = getNpcs().closest("Fishing spot");
        if (fishingSpot != null && fishingSpot.interact("Net")) {
            long rawShrimpCount = getInventory().getAmount("Raw shrimps");
            Sleep.sleepUntil(() -> getInventory().getAmount("Raw shrimps") > rawShrimpCount, 10_000, 600);
        }
    }

    private void lightFire() {
        if (standingOnFire()) {
            getEmptyPosition().ifPresent(position -> {
                WalkingEvent walkingEvent = new WalkingEvent(position);
                walkingEvent.setMinDistanceThreshold(0);
                execute(walkingEvent);
            });
        } else if (!"Tinderbox".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Tinderbox").interact("Use");
        } else if (getInventory().getItem("Logs").interact()) {
            Position playerPos = myPosition();
            Sleep.sleepUntil(() -> !myPosition().equals(playerPos), 10_000, 600);
        }
    }

    private boolean standingOnFire() {
        return getObjects().singleFilter(getObjects().getAll(), obj -> obj.getPosition().equals(myPosition()) && obj.getName().equals("Fire")) != null;
    }

    private Optional<Position> getEmptyPosition() {
        List<Position> allPositions = myPlayer().getArea(10).getPositions();

        // Remove any position with an object (except ground decorations, as they can be walked on)
        for (RS2Object object : getObjects().getAll()) {
            if (object instanceof GroundDecoration) {
                continue;
            }
            allPositions.removeIf(position -> object.getPosition().equals(position));
        }

        allPositions.removeIf(position -> !getMap().canReach(position));

        return allPositions.stream().min(Comparator.comparingInt(p -> myPosition().distance(p)));
    }

    private void cook() {
        if (!"Raw shrimps".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Raw shrimps").interact("Use");
        } else {
            RS2Object fire = getObjects().closest("Fire");
            if (fire != null && fire.interact("Use")) {
                long rawShrimpCount = getInventory().getAmount("Raw shrimps");
                Sleep.sleepUntil(() -> getInventory().getAmount("Raw shrimps") < rawShrimpCount, 5000, 600);
            }
        }
    }
}
