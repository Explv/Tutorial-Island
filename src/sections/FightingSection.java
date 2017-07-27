package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Tab;
import utils.CachedWidget;
import utils.Sleep;
import utils.WidgetActionFilter;

public final class FightingSection extends TutorialSection {

    private static final Area LADDER_AREA = new Area(3108, 9523, 3114, 9529);
    private static final Area INSIDE_RAT_CAGE_GATE_AREA = new Area(3107, 9517, 3110, 9520);
    private static final Area OUTSIDE_RAT_CAGE_GATE_AREA = new Area(3111, 9516, 3113, 9521);

    private final CachedWidget VIEW_EQUIPMENT_STATS_WIDGET = new CachedWidget(new WidgetActionFilter("View equipment stats"));

    public FightingSection() {
        super("Combat Instructor");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
                case 370:
                    talkToInstructor();
                    break;
                case 390:
                    getTabs().open(Tab.EQUIPMENT);
                    break;
                case 400:
                    VIEW_EQUIPMENT_STATS_WIDGET.get(getWidgets()).ifPresent(widget -> {
                        if (widget.interact()) {
                            Sleep.sleepUntil(() -> getProgress() != 400, 3000);
                        }
                    });
                    break;
                case 405:
                    wieldItem("Bronze dagger");
                    break;
                case 410:
                    talkToInstructor();
                    break;
                case 420:
                    if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Bronze sword")) {
                        wieldItem("Bronze sword");
                    } else if (!getEquipment().isWearingItem(EquipmentSlot.SHIELD, "Wooden shield")) {
                        wieldItem("Wooden shield");
                    }
                    break;
                case 430:
                    getTabs().open(Tab.ATTACK);
                    break;
                case 440:
                    enterRatCage();
                    break;
                case 450:
                case 460:
                    if (!inRatCage()) {
                        enterRatCage();
                    } else if (!isAttackingRat()) {
                        attackRat();
                    }
                    break;
                case 470:
                    if (inRatCage()) {
                        leaveRatCage();
                    } else {
                        talkToInstructor();
                    }
                    break;
                case 480:
                case 490:
                    if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Shortbow")) {
                        wieldItem("Shortbow");
                    } else if (!getEquipment().isWearingItem(EquipmentSlot.ARROWS, "Bronze arrow")) {
                        wieldItem("Bronze arrow");
                    } else if (!isAttackingRat()) {
                        attackRat();
                    }
                    break;
                case 500:
                    if (!LADDER_AREA.contains(myPosition())) {
                        getWalking().walk(LADDER_AREA);
                    } else if (getObjects().closest("Ladder").interact("Climb-up")) {
                        Sleep.sleepUntil(() -> !LADDER_AREA.contains(myPosition()), 5000);
                    }
                    break;
            }
    }

    private boolean inRatCage() {
        return !getMap().canReach(getNpcs().closest("Combat Instructor"));
    }

    private void enterRatCage() {
        if (!OUTSIDE_RAT_CAGE_GATE_AREA.contains(myPosition())) {
            getWalking().walk(OUTSIDE_RAT_CAGE_GATE_AREA);
        } else if (getObjects().closest("Gate").interact("Open")) {
            Sleep.sleepUntil(this::inRatCage, 5000);
        }
    }

    private void leaveRatCage() {
        if (!INSIDE_RAT_CAGE_GATE_AREA.contains(myPosition())) {
            getWalking().walk(INSIDE_RAT_CAGE_GATE_AREA);
        } else if (getObjects().closest("Gate").interact("Open")) {
            Sleep.sleepUntil(() -> !inRatCage(), 5000);
        }
    }

    private boolean isAttackingRat() {
        return myPlayer().getInteracting() != null && myPlayer().getInteracting().getName().equals("Giant rat");
    }

    private void attackRat() {
        //noinspection unchecked
        NPC giantRat = getNpcs().closest(npc -> npc.getName().equals("Giant rat") && npc.isAttackable());
        if (giantRat != null && giantRat.interact("Attack")) {
            Sleep.sleepUntil(() -> myPlayer().getInteracting() != null, 5000);
        }
    }

    private void wieldItem(String name) {
        if (getInventory().getItem(name).interact("Wield")) {
            Sleep.sleepUntil(() -> getEquipment().contains(name), 1500);
        }
    }
}
