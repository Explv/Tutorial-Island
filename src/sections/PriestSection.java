package sections;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;

public final class PriestSection extends TutorialSection {

    private static final Area church = new Area(3120, 3103, 3128, 3110);

    public PriestSection() {
        super("Brother Brace");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
            case 550:
                if (!church.contains(myPosition())) {
                    getWalking().webWalk(church);
                } else {
                    talkToInstructor();
                }
                break;
            case 560:
                getTabs().open(Tab.PRAYER);
                break;
            case 570:
                talkToInstructor();
                break;
            case 580:
                getTabs().open(Tab.FRIENDS);
                break;
            case 590:
                getTabs().open(Tab.IGNORES);
                break;
            case 600:
                talkToInstructor();
                break;
            case 610:
                getWalking().webWalk(new Position(3122, 3101, 0));
                break;
        }
    }
}
