package util.widget.filters;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;

import java.util.stream.Stream;

public class WidgetMessageFilter implements Filter<RS2Widget> {

    private final String[] messages;

    public WidgetMessageFilter(final String... messages) {
        this.messages = messages;
    }

    @Override
    public boolean match(final RS2Widget widget) {
        return Stream.of(messages)
                .anyMatch(message -> widget.getMessage().contains(message));
    }
}
