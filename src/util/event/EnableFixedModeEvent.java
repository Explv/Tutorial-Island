package util.event;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;
import util.Sleep;
import util.widget.CachedWidget;
import util.widget.filters.WidgetActionFilter;

public class EnableFixedModeEvent extends Event {

    private final CachedWidget fixedModeWidget = new CachedWidget(new WidgetActionFilter("Fixed mode"));
    private final CachedWidget displaySettingsWidget = new CachedWidget(new WidgetActionFilter("Display"));

    public static boolean isFixedModeEnabled(final MethodProvider methods) {
        return methods.getWidgets().isVisible(378) ||
                methods.getWidgets().isVisible(548) ||
                !methods.myPlayer().isVisible();
    }

    @Override
    public int execute() throws InterruptedException {
        if (isFixedModeEnabled(this)) {
            setFinished();
        } else if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!fixedModeWidget.isVisible(getWidgets())) {
            displaySettingsWidget.interact(getWidgets());
        } else if (fixedModeWidget.interact(getWidgets(), "Fixed mode")) {
            Sleep.sleepUntil(() -> isFixedModeEnabled(this), 3000);
        }
        return 200;
    }
}
