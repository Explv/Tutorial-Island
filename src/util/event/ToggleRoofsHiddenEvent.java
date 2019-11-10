package util.event;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import util.widget.CachedWidget;
import util.widget.filters.WidgetActionFilter;

public final class ToggleRoofsHiddenEvent extends Event {

    private final CachedWidget advancedOptionsWidget = new CachedWidget("Advanced options");
    private final CachedWidget displaySettingsWidget = new CachedWidget(new WidgetActionFilter("Display"));
    private final CachedWidget toggleRoofHiddenWidget = new CachedWidget(new WidgetActionFilter("Roof-removal"));

    private boolean toggledRoofs;

    @Override
    public final int execute() throws InterruptedException {
        if (toggledRoofs) {
            if (getWidgets().closeOpenInterface()) {
                setFinished();
            }
        } else if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!advancedOptionsWidget.isVisible(getWidgets())) {
            displaySettingsWidget.interact(getWidgets());
        } else if (!toggleRoofHiddenWidget.isVisible(getWidgets())) {
            advancedOptionsWidget.interact(getWidgets());
        } else if (!toggledRoofs && toggleRoofHiddenWidget.interact(getWidgets())) {
            toggledRoofs = true;
        }
        return 200;
    }
}
