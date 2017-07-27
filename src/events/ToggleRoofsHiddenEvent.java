package events;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import utils.CachedWidget;
import utils.WidgetActionFilter;

public final class ToggleRoofsHiddenEvent extends Event {

    private final CachedWidget advancedOptionsWidget = new CachedWidget("Advanced options");
    private final CachedWidget displaySettingsWidget = new CachedWidget(new WidgetActionFilter("Display"));
    private final CachedWidget toggleRoofHiddenWidget = new CachedWidget(new WidgetActionFilter("Roof-removal"));

    @Override
    public final int execute() throws InterruptedException {
        if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!advancedOptionsWidget.get(getWidgets()).isPresent()) {
            displaySettingsWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else if (!toggleRoofHiddenWidget.get(getWidgets()).isPresent()) {
            advancedOptionsWidget.get(getWidgets()).get().interact();
        } else if (toggleRoofHiddenWidget.get(getWidgets()).get().interact()) {
            setFinished();
        }
        return 200;
    }
}
