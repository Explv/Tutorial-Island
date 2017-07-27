package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import utils.Sleep;

public final class SurvivalSection extends TutorialSection {

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
                chopTree();
                break;
            case 50:
                lightFire();
                break;
            case 60:
                getTabs().open(Tab.SKILLS);
                break;
            case 70:
                talkToInstructor();
                break;
            case 80:
            case 90:
            case 100:
            case 110:
                if (getTabs().getOpen() != Tab.INVENTORY) {
                    getTabs().open(Tab.INVENTORY);
                } else if (getInventory().getAmount(item -> item.getName().contains("shrimp")) < 2) {
                    fish();
                } else if (getObjects().closest("Fire") == null) {
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
                getWalking().webWalk(new Area(3073, 3083, 3078, 3086));
                break;
        }
    }

    private void chopTree() {
        Entity tree = getObjects().closest("Tree");
        if (tree != null && tree.interact("Chop down")) {
            Sleep.sleepUntil(() -> getInventory().contains("Logs") || !tree.exists(), 10_000);
        }
    }

    private void fish() {
        NPC fishingSpot = getNpcs().closest("Fishing spot");
        if (fishingSpot != null && fishingSpot.interact("Net")) {
            long rawShrimpCount = getInventory().getAmount("Raw shrimps");
            Sleep.sleepUntil(() -> getInventory().getAmount("Raw shrimps") > rawShrimpCount, 10_000);
        }
    }

    private void lightFire() {
        if (!"Tinderbox".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Tinderbox").interact("Use");
        } else if (getInventory().getItem("Logs").interact()) {
            Position playerPos = myPosition();
            Sleep.sleepUntil(() -> !myPosition().equals(playerPos), 10_000);
        }
    }

    private void cook() {
        if (!"Raw shrimps".equals(getInventory().getSelectedItemName())) {
            getInventory().getItem("Raw shrimps").interact("Use");
        } else {
            RS2Object fire = getObjects().closest("Fire");
            if (fire != null && fire.interact("Use")) {
                long rawShrimpCount = getInventory().getAmount("Raw shrimps");
                Sleep.sleepUntil(() -> getInventory().getAmount("Raw shrimps") < rawShrimpCount, 5000);
            }
        }
    }
}
