package events;

import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;
import utils.CachedWidget;
import utils.Sleep;
import utils.WidgetActionFilter;

public class EnableFixedModeEvent extends Event {

    private final CachedWidget optionsWidget = new CachedWidget(new WidgetActionFilter("Options"));
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
        } else if (!optionsWidget.get(getWidgets()).isPresent()) {
            setFailed();
        } else if (!displaySettingsWidget.get(getWidgets()).isPresent()) {
            if (optionsWidget.get(getWidgets()).get().interact("Options")) {
                Sleep.sleepUntil(() -> displaySettingsWidget.get(getWidgets()).isPresent(), 3000);
            }
        } else if (!fixedModeWidget.get(getWidgets()).isPresent()) {
            displaySettingsWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else if (fixedModeWidget.get(getWidgets()).get().interact("Fixed mode")) {
            Sleep.sleepUntil(() -> isFixedModeEnabled(this), 3000);
        }
        return 200;
    }
}
