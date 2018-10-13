package events;

import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import utils.CachedWidget;
import utils.WidgetActionFilter;

public final class ToggleShiftDropEvent extends Event {

    private final CachedWidget toggleShiftClickDrop = new CachedWidget(new WidgetActionFilter("Toggle shift click to drop"));

    @Override
    public final int execute() throws InterruptedException {
        if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!toggleShiftClickDrop.get(getWidgets()).isPresent()) {
            getSettings().open(Settings.SettingsTab.CONTROLS);
        } else if (toggleShiftClickDrop.get(getWidgets()).get().interact()) {
            setFinished();
        }
        return 200;
    }
}
