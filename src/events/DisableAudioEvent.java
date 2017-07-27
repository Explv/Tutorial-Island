package events;

import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import utils.CachedWidget;
import utils.WidgetActionFilter;

public final class DisableAudioEvent extends Event {

    private final CachedWidget soundSettingsWidget = new CachedWidget(new WidgetActionFilter("Audio"));
    private final CachedWidget musicVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Music Volume"));
    private final CachedWidget soundEffectVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Sound Effect Volume"));
    private final CachedWidget areaSoundEffectVolumeWidget = new CachedWidget(new WidgetActionFilter("Adjust Area Sound Effect Volume"));

    private static final int musicVolumeConfig = 168;
    private static final int soundEffectVolumeConfig = 169;
    private static final int areaSoundEffectVolumeConfig = 872;

    @Override
    public final int execute() throws InterruptedException {
        if (Tab.SETTINGS.isDisabled(getBot())) {
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if (!musicVolumeWidget.get(getWidgets()).isPresent()) {
            soundSettingsWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else if (!isVolumeDisabled(musicVolumeConfig)) {
            musicVolumeWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else if (!isVolumeDisabled(soundEffectVolumeConfig)) {
            soundEffectVolumeWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else if (!isVolumeDisabled(areaSoundEffectVolumeConfig)) {
            areaSoundEffectVolumeWidget.get(getWidgets()).ifPresent(widget -> widget.interact());
        } else {
            setFinished();
        }
        return 200;
    }

    private boolean isVolumeDisabled(final int config) {
        return getConfigs().get(config) == 4;
    }
}
