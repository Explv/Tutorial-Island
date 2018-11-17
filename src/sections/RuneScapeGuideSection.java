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
import utils.WidgetActionFilter;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class RuneScapeGuideSection extends TutorialSection {

    private final CachedWidget nameLookupWidget = new CachedWidget(new WidgetActionFilter("Look up name"));
    private final CachedWidget checkNameWidget = new CachedWidget(w -> w.getMessage().contains("What name would you like to check"));
    private final CachedWidget suggestedNameWidget = new CachedWidget("suggestions");
    private final CachedWidget setNameWidget = new CachedWidget("Set name");
    private final CachedWidget creationScreenWidget = new CachedWidget("Head");
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
            case 1:
            case 2:
                if (getConfigs().get(1042) != 21) {
                    setDisplayName();
                } else if (isCreationScreenVisible()) {
                    createRandomCharacter();
                } else if (experienceWidget.get(getWidgets()).isPresent()) {
                    if (getDialogues().selectOption(random(1, 3))) {
                        Sleep.sleepUntil(() -> !experienceWidget.get(getWidgets()).map(widget -> !widget.isVisible()).orElse(true), 2000, 600);
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

    private void setDisplayName() {
        int configID = 1042;
        int configValue = getConfigs().get(configID);

        switch (configValue) {
            case 0:
            case 1:
                if (suggestedNameWidget.get(getWidgets()).isPresent()) {
                    RS2Widget suggestedWidget = suggestedNameWidget.get(getWidgets()).get();
                    int rootID = suggestedWidget.getRootId();
                    int secondLevelID = suggestedWidget.getSecondLevelId();
                    RS2Widget nameWidget = getWidgets().get(rootID, secondLevelID + 2 + random(0, 2));
                    if (nameWidget.interact()) {
                        Sleep.sleepUntil(() -> getConfigs().get(configID) == 4, 1200);
                    }
                } else if (checkNameWidget.get(getWidgets()).filter(RS2Widget::isVisible).isPresent()) {
                    if (getKeyboard().typeString(generateRandomString(4))) {
                        Sleep.sleepUntil(() -> getConfigs().get(configID) == 2, 1200);
                    }
                } else if (nameLookupWidget.get(getWidgets()).get().interact("Look up name")) {
                    Sleep.sleepUntil(() -> getConfigs().get(configID) == 1, 1200);
                }
                break;
            case 4:
                if (setNameWidget.get(getWidgets()).isPresent()) {
                    if (setNameWidget.get(getWidgets()).get().interact()) {
                        Sleep.sleepUntil(() -> getConfigs().get(configID) == 21, 2400);
                    }
                }
            default:
                Sleep.sleepUntil(() -> getConfigs().get(1042) != configValue, 1200);
        }
    }

    private String generateRandomString(int maxLength) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                     + "abcdefghijklmnopqrstuvwxyz"
                     + "0123456789";
        return new Random().ints(new Random().nextInt(maxLength) + 1, 0, chars.length())
                           .mapToObj(i -> "" + chars.charAt(i))
                           .collect(Collectors.joining());
    }

    private boolean isCreationScreenVisible() {
        return creationScreenWidget.get(getWidgets()).filter(RS2Widget::isVisible).isPresent();
    }

    private void createRandomCharacter() throws InterruptedException {
        // letting all the widgets show up
        sleep(2000);

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
            Sleep.sleepUntil(() -> !isCreationScreenVisible(), 3000, 600);
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
