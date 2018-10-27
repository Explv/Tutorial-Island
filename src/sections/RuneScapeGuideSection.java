package sections;

import events.DisableAudioEvent;
import events.EnableFixedModeEvent;
import events.ToggleRoofsHiddenEvent;
import events.ToggleShiftDropEvent;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;
import utils.CachedWidget;
import utils.Sleep;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public final class RuneScapeGuideSection extends TutorialSection {

    private final CachedWidget creationScreenWidget = new CachedWidget("Welcome to RuneScape");
    private final CachedWidget experienceWidget = new CachedWidget("What's your experience with Old School Runescape?");
    private boolean isAudioDisabled;

    public RuneScapeGuideSection() {
        super("Gielinor Guide");
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
                } else if (experienceWidget.get(getWidgets()).isPresent()) {
                    if (getDialogues().selectOption(random(1, 3))) {
                        Sleep.sleepUntil(() -> !experienceWidget.get(getWidgets()).map(widget -> !widget.isVisible()).orElse(true), 2000);
                    }
                } else {
                    talkToInstructor();
                }
                break;
            case 3:
                if (!EnableFixedModeEvent.isFixedModeEnabled(this)) {
                    if (execute(new EnableFixedModeEvent()).hasFinished()) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(getBot().getBotPanel(), "Please restart the client"));
                        getBot().getScriptExecutor().stop();
                    }
                } else {
                    getTabs().open(Tab.SETTINGS);
                }
                break;
            case 10:
                if (!isAudioDisabled) {
                    isAudioDisabled = disableAudio();
                } else if (!getSettings().areRoofsEnabled()) {
                    toggleRoofsHidden();
                } else if (!getSettings().isShiftDropActive()) {
                    toggleShiftDrop();
                } else if (getObjects().closest("Door").interact("Open")) {
                    Sleep.sleepUntil(() -> getProgress() != 10, 5000, 600);
                }
                break;
            default:
                talkToInstructor();
                break;
        }
    }

    private boolean creationScreenIsVisible() {
        return creationScreenWidget.get(getWidgets()).filter(RS2Widget::isVisible).isPresent();
    }

    private void createRandomCharacter() throws InterruptedException {
        if (new Random().nextInt(2) == 1) {
            getWidgets().getWidgetContainingText("Female").interact();
        }

        final RS2Widget[] childWidgets = getWidgets().getWidgets(creationScreenWidget.get(getWidgets()).get().getRootId());
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
            Sleep.sleepUntil(() -> !creationScreenIsVisible(), 3000, 600);
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

    private boolean toggleShiftDrop() {
        Event toggleShiftDrop = new ToggleShiftDropEvent();
        execute(toggleShiftDrop);
        return toggleShiftDrop.hasFinished();
    }
}
