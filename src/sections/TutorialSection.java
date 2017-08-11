package sections;

import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import utils.Sleep;

import java.awt.event.KeyEvent;

public abstract class TutorialSection extends MethodProvider {

    private final String INSTRUCTOR_NAME;

    public TutorialSection(final String INSTRUCTOR_NAME) {
        this.INSTRUCTOR_NAME = INSTRUCTOR_NAME;
    }

    public abstract void onLoop() throws InterruptedException;

    protected final int getProgress() {
        return getConfigs().get(281);
    }

    protected final void talkToInstructor() {
        if (getInstructor().interact("Talk-to")) {
            Sleep.sleepUntil(this::pendingContinue, 5000);
        }
    }

    protected NPC getInstructor() {
        return getNpcs().closest(INSTRUCTOR_NAME);
    }

    protected boolean pendingContinue() {
        RS2Widget continueWidget = getContinueWidget();
        return continueWidget!= null && continueWidget.isVisible();
    }

    protected boolean selectContinue() {
        RS2Widget continueWidget = getContinueWidget();
        if (continueWidget == null) {
            return false;
        }
        if (continueWidget.getMessage().contains("Click here to continue")) {
            getKeyboard().pressKey(KeyEvent.VK_SPACE);
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000);
            return true;
        } else if (continueWidget.interact()) {
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000);
            return true;
        }
        return false;
    }

    private RS2Widget getContinueWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(),
                widget -> widget.isVisible()
                        && (widget.getMessage().contains("Click here to continue")
                        || widget.getMessage().contains("Click to continue"))
        );
    }
}
