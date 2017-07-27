package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WalkingEvent;
import utils.Sleep;

public final class WizardSection extends TutorialSection {

    private static final Area WIZARD_BUILDING = new Area(new int[][]{
            {3140, 3085}, {3143, 3088},
            {3140, 3083}, {3141, 3084},
            {3140, 3089}, {3143, 3089},
            {3137, 3091}, {3141, 3091},
            {3138, 3090}, {3142, 3090},
            {3139, 3089}, {3140, 3089},
            {3141, 3084}, {3143, 3084},
            {3141, 3083}, {3142, 3083},
            {3138, 3082}, {3141, 3082},
            {3138, 3083}, {3140, 3083},
            {3139, 3084}, {3141, 3084}
    });

    private static final Area CHICKEN_AREA = new Area(3139, 3091, 3140, 3090);

    public WizardSection() {
        super("Magic Instructor");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        if (!WIZARD_BUILDING.contains(myPosition())) {
            getWalking().webWalk(WIZARD_BUILDING);
        }

        switch (getProgress()) {
            case 620:
                talkToInstructor();
                break;
            case 630:
                getTabs().open(Tab.MAGIC);
                break;
            case 640:
                talkToInstructor();
                break;
            case 650:
                if (!CHICKEN_AREA.contains(myPosition())) {
                    walkToChickenArea();
                } else {
                    attackChicken();
                }
                break;
            case 670:
                if (getDialogues().isPendingOption()) {
                    getDialogues().selectOption("Yes.", "I'm fine, thanks.");
                } else {
                    talkToInstructor();
                }
                break;
        }
    }

    private boolean walkToChickenArea() {
        WalkingEvent walkingEvent = new WalkingEvent(CHICKEN_AREA);
        walkingEvent.setMinDistanceThreshold(0);
        walkingEvent.setMinDistanceThreshold(0);
        execute(walkingEvent);
        return walkingEvent.hasFinished();
    }

    private boolean attackChicken() {
        NPC chicken = getNpcs().closest("Chicken");
        if (chicken != null && getMagic().castSpellOnEntity(Spells.NormalSpells.WIND_STRIKE, chicken)) {
            Sleep.sleepUntil(() -> getProgress() != 650, 3000);
            return true;
        }
        return false;
    }
}