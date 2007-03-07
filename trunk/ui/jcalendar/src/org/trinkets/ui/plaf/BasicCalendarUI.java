package org.trinkets.ui.plaf;

import org.trinkets.ui.JCalendar;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Basic CalendarUI implementation.
 *
 * @author Alexey Efimov
 */
public class BasicCalendarUI extends CalendarUI {
    // Shared UI object
    private static CalendarUI ui;

    public void installUI(JComponent c) {
        JCalendar calendar = (JCalendar) c;
        super.installUI(calendar);
        installDefaults(calendar);
    }

    public void uninstallUI(JComponent c) {
        JCalendar calendar = (JCalendar) c;
        uninstallDefaults(calendar);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        JCalendar calendar = (JCalendar) c;
        
        super.paint(g, c);
    }

    protected void installDefaults(JCalendar calendar) {
        LookAndFeel.installColorsAndFont(calendar,
                "Calendar.background",
                "Calendar.foreground",
                "Calendar.font");
        LookAndFeel.installBorder(calendar, "Calendar.border");
        LookAndFeel.installProperty(calendar, "opaque", Boolean.FALSE);
    }

    protected void uninstallDefaults(JCalendar calendar) {
        LookAndFeel.uninstallBorder(calendar);
    }

    public static ComponentUI createUI(JComponent c) {
        if (ui == null) {
            ui = new BasicCalendarUI();
        }
        return ui;
    }

}
