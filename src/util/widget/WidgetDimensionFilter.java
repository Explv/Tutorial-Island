package util.widget.filters;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;

public class WidgetDimensionFilter implements Filter<RS2Widget> {

    private final int width, height;

    public WidgetDimensionFilter(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean match(final RS2Widget widget) {
        return widget.getWidth() == width && widget.getHeight() == height;
    }
}
