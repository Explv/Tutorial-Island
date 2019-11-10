package util.widget.filters;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;

public final class WidgetActionFilter implements Filter<RS2Widget> {

    private final String[] actions;

    public WidgetActionFilter(final String... actions) {
        this.actions = actions;
    }

    @Override
    public final boolean match(final RS2Widget rs2Widget) {
        if (rs2Widget == null || !rs2Widget.isVisible() || rs2Widget.getInteractActions() == null) {
            return false;
        }
        for (final String widgetAction : rs2Widget.getInteractActions()) {
            for (final String matchAction : actions) {
                if (matchAction.equals(widgetAction)) {
                    return true;
                }
            }
        }
        return false;
    }
}