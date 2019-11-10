package util.widget;

import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.ui.RS2Widget;

import java.util.Optional;

/**
 * This class provides a clean and efficient way of accessing RS2Widgets.
 * Widgets can be found using IDs, the message, or any other Filter<RS2Widget>
 * Once the RS2Widget is found, it's IDs are stored, so any future lookup can be performed in O(1) time.
 * <p>
 * Note that the class is thread safe, and so instances of this class may be static.
 */
public class CachedWidget {

    private int rootID = -1, secondLevelID = -1, thirdLevelID = -1;
    private String[] widgetTexts;
    private Filter<RS2Widget> filter;

    /**
     * This *SHOULD NOT* be used for constant widgets. Widget IDs can change, which is the entire purpose
     * of this class!
     *
     * @param rootID        The root ID of the RS2Widget to cache
     * @param secondLevelID The second level ID of the RS2Widget to cache
     */
    public CachedWidget(final int rootID, final int secondLevelID) {
        this.rootID = rootID;
        this.secondLevelID = secondLevelID;
    }

    /**
     * This *SHOULD NOT* be used for constant widgets. Widget IDs can change, which is the entire purpose
     * of this class!
     *
     * @param rootID        The root ID of the RS2Widget to cache
     * @param secondLevelID The second level ID of the RS2Widget to cache
     * @param thirdLevelID  The third level ID of the RS2Widget to cache
     */
    public CachedWidget(final int rootID, final int secondLevelID, final int thirdLevelID) {
        this.rootID = rootID;
        this.secondLevelID = secondLevelID;
        this.thirdLevelID = thirdLevelID;
    }

    /**
     * @param rootID      The root ID of the RS2Widget to cache
     * @param widgetTexts An array of widget messages to match when searching for the RS2Widget to cache
     */
    public CachedWidget(final int rootID, final String... widgetTexts) {
        this.rootID = rootID;
        this.widgetTexts = widgetTexts;
    }

    /**
     * @param widgetTexts An array of widget messages to match when searching for the RS2Widget to cache
     */
    public CachedWidget(final String... widgetTexts) {
        this.widgetTexts = widgetTexts;
    }

    /**
     * @param rootID The root ID of the RS2Widget to cache
     * @param filter A Filter<RS2Widget> to match when searching for the RS2Widget to cache
     */
    public CachedWidget(final int rootID, final Filter<RS2Widget> filter) {
        this.rootID = rootID;
        this.filter = filter;
    }

    /**
     * @param filter A Filter<RS2Widget> to match when searching for the RS2Widget to cache
     */
    public CachedWidget(final Filter<RS2Widget> filter) {
        this.filter = filter;
    }

    /**
     * @param rs2Widget An RS2Widget to cache
     */
    public CachedWidget(final RS2Widget rs2Widget) {
        setWidgetIDs(rs2Widget);
    }

    /**
     * Returns whether the cached RS2Widget is not null, and is visible
     *
     * @param widgets OSBot Widgets API
     * @return True if the cached RS2Widget is not null and visible
     */
    public boolean isVisible(final Widgets widgets) {
        return get(widgets).map(RS2Widget::isVisible)
                .orElse(false);
    }

    /**
     * Interacts with the cached RS2Widget if it is visible
     *
     * @param widgets     OSBot Widgets API
     * @param interaction Array of interaction options
     * @return True if the cached RS2Widget is visible and the interaction was successful, else false
     */
    public boolean interact(final Widgets widgets, final String... interaction) {
        return get(widgets).filter(RS2Widget::isVisible)
                .map(w -> w.interact(interaction))
                .orElse(false);
    }

    /**
     * Returns the parent RS2Widget of the cached widget
     * If the CachedWidget is a second level RS2Widget, the same RS2Widget will be returned
     * as you cannot have an RS2Widget with just a root ID.
     *
     * @param widgets OSBot Widgets API
     * @return An Optional parent RS2Widget
     */
    public Optional<RS2Widget> getParent(final Widgets widgets) {
        return get(widgets).map(widget -> {
            if (widget.isSecondLevel()) {
                return widget;
            }
            return widgets.get(widget.getRootId(), widget.getSecondLevelId());
        });
    }

    /**
     * Returns an RS2Widget relative to the cached widget
     *
     * @param widgets             OSBot Widgets API
     * @param rootModifier        Relative root ID modifier
     * @param secondLevelModifier Relative second level ID modifier
     * @param thirdLevelModifier  Relative third level ID modifier
     * @return An Optional relative RS2Widget
     */
    public Optional<RS2Widget> getRelative(final Widgets widgets,
                                           final int rootModifier,
                                           final int secondLevelModifier,
                                           final int thirdLevelModifier) {
        return get(widgets).map(widget -> {
            if (widget.isThirdLevel()) {
                return widgets.get(
                        rootID + rootModifier,
                        secondLevelID + secondLevelModifier,
                        thirdLevelID + thirdLevelModifier
                );
            }
            return widgets.get(
                    rootID + rootModifier,
                    secondLevelID + secondLevelModifier
            );
        });
    }

    /**
     * Returns the cached RS2Widget.
     * <p>
     * If no RS2Widget has been cached, it tries to find the RS2Widget using the supplied
     * filter / message string / etc.
     * <p>
     * If the RS2Widget is found, it is cached and then returned
     * <p>
     * If no RS2Widget is found, an empty Optional is returned.
     *
     * @param widgets OSBot Widgets API
     * @return The Optional cached RS2Widget
     */
    public Optional<RS2Widget> get(final Widgets widgets) {
        if (rootID != -1 && secondLevelID != -1 && thirdLevelID != -1) {
            return Optional.ofNullable(widgets.get(rootID, secondLevelID, thirdLevelID));
        } else if (rootID != -1 && secondLevelID != -1) {
            return Optional.ofNullable(widgets.get(rootID, secondLevelID));
        } else if (widgetTexts != null) {
            return getWidgetWithText(widgets);
        } else {
            return getWidgetUsingFilter(widgets);
        }
    }

    /**
     * Finds and caches a widget using the specified message Strings
     * <p>
     * If no RS2Widget is found, an empty Optional is returned
     *
     * @param widgets OSBot Widgets API
     * @return The cached RS2Widget
     */
    private Optional<RS2Widget> getWidgetWithText(final Widgets widgets) {
        RS2Widget rs2Widget;
        if (rootID != -1) {
            rs2Widget = widgets.getWidgetContainingText(rootID, widgetTexts);
        } else {
            rs2Widget = widgets.getWidgetContainingText(widgetTexts);
        }
        setWidgetIDs(rs2Widget);
        return Optional.ofNullable(rs2Widget);
    }

    /**
     * Finds and caches a widget using the specified Filter<RS2Widget>
     * <p>
     * If no RS2Widget is found, an empty Optional is returned
     *
     * @param widgets OSBot Widgets API
     * @return The cached RS2Widget
     */
    private Optional<RS2Widget> getWidgetUsingFilter(final Widgets widgets) {
        RS2Widget rs2Widget;
        if (rootID != -1) {
            rs2Widget = widgets.singleFilter(rootID, filter);
        } else {
            rs2Widget = widgets.singleFilter(widgets.getAll(), filter);
        }
        setWidgetIDs(rs2Widget);
        return Optional.ofNullable(rs2Widget);
    }

    /**
     * Caches an RS2Widget's IDs
     *
     * @param rs2Widget The RS2Widget to cache
     */
    private synchronized void setWidgetIDs(final RS2Widget rs2Widget) {
        if (rs2Widget == null) {
            return;
        }
        rootID = rs2Widget.getRootId();
        secondLevelID = rs2Widget.getSecondLevelId();
        if (rs2Widget.isThirdLevel()) {
            thirdLevelID = rs2Widget.getThirdLevelId();
        }
    }

    @Override
    public String toString() {
        return rootID + ", " + secondLevelID + ", " + thirdLevelID;
    }
} 