package org.trinkets.ui.plaf;

import org.jetbrains.annotations.NotNull;
import org.trinkets.ui.JCalendarDayList;

import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * UI for {@link org.trinkets.ui.JCalendarDayList}
 *
 * @author Alexey Efimov
 */
public abstract class CalendarDayListUI extends ComponentUI {
    @NotNull
    public abstract Point toCellPoint(@NotNull JCalendarDayList c, @NotNull Point point);
}
