package sections;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;
import events.DisableAudioEvent;
import utils.CachedWidget;
import utils.Sleep;
import events.ToggleRoofsHiddenEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public final class RuneScapeGuideSection extends TutorialSection {

    private final CachedWidget CREATION_SCREEN_WIDGET = new CachedWidget("Welcome to RuneScape");
    private boolean isAudioDisabled;

    public RuneScapeGuideSection() {
        super("RuneScape Guide");
    }

    @Override
    public final void onLoop() throws InterruptedException {
        if (pendingContinue()) {
            selectContinue();
            return;
        }

        switch (getProgress()) {
            case 0:
                if (creationScreenIsVisible()) {
                    createRandomCharacter();
                } else if (getDialogues().isPendingOption()) {
                    getDialogues().selectOption(3);
                } else {
                    talkToInstructor();
                }
                break;
            case 3:
                getTabs().open(Tab.SETTINGS);
                break;
            case 10:
                if (!isAudioDisabled) {
                    isAudioDisabled = disableAudio();
                } else if (!getSettings().areRoofsEnabled()) {
                    toggleRoofsHidden();
                } else if (getObjects().closest("Door").interact("Open")) {
                    Sleep.sleepUntil(() -> getProgress() != 10, 5000);
                }
                break;
            default:
                talkToInstructor();
                break;
        }
    }

    private boolean creationScreenIsVisible() {
        return CREATION_SCREEN_WIDGET.get(getWidgets()).filter(RS2Widget::isVisible).isPresent();
    }

    private void createRandomCharacter() throws InterruptedException {

        if (new Random().nextInt(2) == 1) {
            getWidgets().getWidgetContainingText("Female").interact();
        }

        final RS2Widget[] childWidgets = getWidgets().getWidgets(CREATION_SCREEN_WIDGET.get(getWidgets()).get().getRootId());
        Collections.shuffle(Arrays.asList(childWidgets));

        for (final RS2Widget childWidget : childWidgets) {
            if (childWidget.getToolTip() == null) {
                continue;
            }
            if (childWidget.getToolTip().contains("Change") || childWidget.getToolTip().contains("Recolour")) {
                clickRandomTimes(childWidget);
            }
        }

        if (getWidgets().getWidgetContainingText("Accept").interact()) {
            Sleep.sleepUntil(() -> !creationScreenIsVisible(), 3000);
        }
    }

    private void clickRandomTimes(final RS2Widget widget) throws InterruptedException {
        int clickCount = new Random().nextInt(4);

        for (int i = 0; i < clickCount; i++) {
            if (widget.interact()) {
                MethodProvider.sleep(150);
            }
        }
    }

    private boolean disableAudio() {
        Event disableAudioEvent = new DisableAudioEvent();
        execute(disableAudioEvent);
        return disableAudioEvent.hasFinished();
    }

    private boolean toggleRoofsHidden() {
        Event toggleRoofsHiddenEvent = new ToggleRoofsHiddenEvent();
        execute(toggleRoofsHiddenEvent);
        return toggleRoofsHiddenEvent.hasFinished();
    }
}
