package util.event;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import util.widget.CachedWidget;
import util.widget.filters.WidgetActionFilter;

public final class DisableAudioEvent extends Event {

    private static final int musicVolumeConfig = 168;
    private static final int soundEffectVolumeConfig = 169;
    private static final int areaSoundEffectVolumeConfig = 872;

    private final CachedWidget soundSettingsWidget = new CachedWidget(new WidgetActionFilter("Audio"));
    private final CachedWidget musicVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Music Volume"));
    private final CachedWidget soundEffectVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Sound Effect Volume"));
    private final CachedWidget areaSoundEffectVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Area Sound Effect Volume"));

    @Override
    public final int execute() throws InterruptedException {
        if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!musicVolumeWidget.isVisible(getWidgets())) {
            soundSettingsWidget.interact(getWidgets());
        } else if (!isVolumeDisabled(musicVolumeConfig)) {
            musicVolumeWidget.interact(getWidgets());
        } else if (!isVolumeDisabled(soundEffectVolumeConfig)) {
            soundEffectVolumeWidget.interact(getWidgets());
        } else if (!isVolumeDisabled(areaSoundEffectVolumeConfig)) {
            areaSoundEffectVolumeWidget.interact(getWidgets());
        } else {
            setFinished();
        }
        return 200;
    }

    private boolean isVolumeDisabled(final int config) {
        return getConfigs().get(config) == 4;
    }
}