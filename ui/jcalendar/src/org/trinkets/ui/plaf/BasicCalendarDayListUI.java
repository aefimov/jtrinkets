package org.trinkets.ui.plaf;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.trinkets.ui.CalendarDayListCellRenderer;
import org.trinkets.ui.CalendarDayListModel;
import org.trinkets.ui.DefaultCalendarDayListModel;
import org.trinkets.ui.JCalendarDayList;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.util.Locale;

/**
 * Basic CalendarDayListUI implementation.
 *
 * @author Alexey Efimov
 */
public class BasicCalendarDayListUI extends CalendarDayListUI {
    @NonNls
    private static final String CLIENT_PROPERTY_HOVER_CELL = "hover.cell";
    @NonNls
    private static final String CLIENT_PROPERTY_SELECTION_CELL = "selection.cell";
    // Shared UI object
    private static CalendarDayListUI ui;

    public void installUI(JComponent c) {
        JCalendarDayList dayList = (JCalendarDayList) c;
        super.installUI(dayList);
        installDefaults(dayList);
    }

    public void uninstallUI(JComponent c) {
        JCalendarDayList dayList = (JCalendarDayList) c;
        uninstallDefaults(dayList);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        JCalendarDayList dayList = (JCalendarDayList) c;
        CalendarDayListModel model = dayList.getModel();
        if (model != null) {

            Dimension size = c.getSize();

            CalendarDayListCellRenderer weekCellRenderer = dayList.getWeekCellRenderer();
            CalendarDayListCellRenderer dayCellRenderer = dayList.getDayCellRenderer();
            int cellWidth = size.width / model.getDaysInWeek();
            int cellHeight = size.height / (model.getWeeksInMonth() + 3);

            Point hoverCell = getHoverCell(dayList);
            Point selectionCell = getSelectionCell(dayList);

            for (int i = 0; i < model.getDaysInWeek(); i++) {
                String weekday = model.getWeekCellValue(i);
                boolean weekend = model.isWeekendCell(i);
                Component comp = weekCellRenderer.getCalendarDayListCellRendererComponent(dayList, weekday, false, false, weekend, false, -1, i);
                int x = i * cellWidth;
                int width = i == model.getDaysInWeek() - 1 ? size.width - x : cellWidth;
                comp.setBounds(x, 0, width, cellHeight);
                Graphics gc = g.create(x, 0, width, cellHeight);
                try {
                    comp.paint(gc);
                } finally {
                    gc.dispose();
                }
                for (int j = -1; j < model.getWeeksInMonth() + 1; j++) {
                    String value = model.getDayCellValue(j, i);
                    int y = (j + 2) * cellHeight;
                    int height = j == model.getWeeksInMonth() ? size.height - y : cellHeight;
                    boolean month = model.isWithinMonthCell(j, i);
                    boolean hasFocus = hoverCell != null && i == hoverCell.x && j + 1 == hoverCell.y;
                    boolean hasSelection = selectionCell != null && i == selectionCell.x && j + 1 == selectionCell.y;
                    comp = dayCellRenderer.getCalendarDayListCellRendererComponent(dayList, value, hasSelection, hasFocus, weekend, month, j, i);
                    comp.setBounds(x, y, width, height);
                    gc = g.create(x, y, width, height);
                    try {
                        comp.paint(gc);
                    } finally {
                        gc.dispose();
                    }
                }
            }
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        return getMinimumSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getMinimumSize(c);
    }

    public Dimension getMinimumSize(JComponent c) {
        JCalendarDayList dayList = (JCalendarDayList) c;
        CalendarDayListModel model = dayList.getModel();
        if (model != null) {
            Font dayFont = dayList.getDayFont();
            FontMetrics metrics = c.getFontMetrics(dayFont);
            int cellWidth = Math.max(metrics.stringWidth("00"), getWeekCellWidth(dayList));
            int cellHeight = metrics.getHeight();

            int width = (cellWidth + 20) * model.getDaysInWeek();
            int height = (cellHeight + 10) * (model.getWeeksInMonth() + 2);
            return new Dimension(width, height);
        }
        return super.getMinimumSize(c);
    }

    public int getWeekCellWidth(JCalendarDayList dayList) {
        int width = 0;
        CalendarDayListModel model = dayList.getModel();
        if (model != null) {
            Font weekFont = dayList.getWeekFont();
            FontMetrics metrics = dayList.getFontMetrics(weekFont);
            int count = model.getDaysInWeek();
            for (int i = 0; i < count; i++) {
                width = Math.max(metrics.stringWidth(model.getWeekCellValue(i)), width);
            }
        }
        return width;
    }

    protected void installDefaults(JCalendarDayList dayList) {
        LookAndFeel.installColorsAndFont(dayList,
                "CalendarDayList.background",
                "CalendarDayList.foreground",
                "CalendarDayList.font");
        LookAndFeel.installBorder(dayList, "CalendarDayList.border");
        LookAndFeel.installProperty(dayList, "opaque", Boolean.FALSE);
        // Model
        if (dayList.getModel() == null) {
            dayList.setModel(new DefaultCalendarDayListModel(Locale.getDefault()));
        }
        // Renderers
        if (dayList.getDayCellRenderer() == null) {
            dayList.setDayCellRenderer(new DayRenderer());
        }
        if (dayList.getWeekCellRenderer() == null) {
            dayList.setWeekCellRenderer(new WeekRenderer());
        }
        // Colors
        dayList.setSelectionForeground(getColor(dayList.getSelectionForeground(), "CalendarDayList.selectionForeground"));
        dayList.setSelectionBackground(getColor(dayList.getSelectionBackground(), "CalendarDayList.selectionBackground"));
        dayList.setHeaderForeground(getColor(dayList.getHeaderForeground(), "CalendarDayList.headerForeground"));
        dayList.setHeaderBackground(getColor(dayList.getHeaderBackground(), "CalendarDayList.headerBackground"));
        dayList.setWeekendForeground(getColor(dayList.getWeekendForeground(), "CalendarDayList.weekendForeground"));
        dayList.setWeekendBackground(getColor(dayList.getWeekendBackground(), "CalendarDayList.weekendBackground"));
        dayList.setOutOfMonthForeground(getColor(dayList.getOutOfMonthForeground(), "CalendarDayList.outOfMonthForeground"));
        dayList.setOutOfMonthBackground(getColor(dayList.getOutOfMonthBackground(), "CalendarDayList.outOfMonthBackground"));
        // Fonts
        dayList.setDayFont(getFont(dayList.getDayFont(), "CalendarDayList.dayFont"));
        dayList.setWeekFont(getFont(dayList.getWeekFont(), "CalendarDayList.weekFont"));
    }

    private static Color getColor(Color color, String key) {
        if (color == null || color instanceof UIResource) {
            return UIManager.getColor(key);
        }
        return color;
    }

    private static Font getFont(Font font, String key) {
        if (font == null || font instanceof UIResource) {
            return UIManager.getFont(key);
        }
        return font;
    }

    protected void uninstallDefaults(JCalendarDayList dayList) {
        LookAndFeel.uninstallBorder(dayList);
    }

    public static ComponentUI createUI(JComponent c) {
        if (c != null) {
            if (ui == null) {
                ui = new BasicCalendarDayListUI();
            }
        }
        return ui;
    }

    private static Color mix(Color sourceColor, Color additionColor) {
        float[] source = sourceColor.getRGBComponents(null);
        float[] addition = additionColor.getRGBComponents(null);
        for (int i = 0; i < addition.length; i++) {
            source[i] = (source[i] + addition[i]) / 2;
        }
        return new Color(source[0], source[1], source[2], source[3]);
    }

    @NotNull
    public Point toCellPoint(@NotNull JCalendarDayList c, @NotNull Point point) {
        Dimension size = c.getSize();
        CalendarDayListModel model = c.getModel();
        if (model != null) {
            int cellWidth = size.width / model.getDaysInWeek();
            int cellHeight = size.height / (model.getWeeksInMonth() + 3);
            point.x = point.x / cellWidth;
            point.y = (point.y / cellHeight) - 1;
        }
        return point;
    }

    public void setHoverCell(@NotNull JCalendarDayList c, Point point) {
        c.putClientProperty(CLIENT_PROPERTY_HOVER_CELL, point);
        setToolTipText(c, point);
    }

    public void setSelectionCell(@NotNull JCalendarDayList c, Point point) {
        c.putClientProperty(CLIENT_PROPERTY_SELECTION_CELL, point);
        setToolTipText(c, point);
    }

    private void setToolTipText(JCalendarDayList c, Point point) {
        CalendarDayListModel model = c.getModel();
        if (model != null) {
            if (point != null) {
                c.setToolTipText(model.getToolTipTextAt(point.y - 1, point.x));
            } else {
                c.setToolTipText(null);
            }
        } else {
            c.setToolTipText(null);
        }
    }

    public Point getHoverCell(@NotNull JCalendarDayList c) {
        return (Point) c.getClientProperty(CLIENT_PROPERTY_HOVER_CELL);
    }

    public Point getSelectionCell(@NotNull JCalendarDayList c) {
        return (Point) c.getClientProperty(CLIENT_PROPERTY_SELECTION_CELL);
    }

    private static class DayRenderer extends JLabel implements CalendarDayListCellRenderer {
        public Component getCalendarDayListCellRendererComponent(JCalendarDayList dayList, Object value, boolean isSelected, boolean hasFocus, boolean weekend, boolean withinMonth, int row, int column) {
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setText((String) value);
            setFont(dayList.getDayFont());
            setOpaque(true);
            setEnabled(dayList.isEnabled());
            if (isSelected) {
                setForeground(dayList.getSelectionForeground());
                setBackground(dayList.getSelectionBackground());
            } else {
                if (weekend) {
                    setForeground(withinMonth ? dayList.getWeekendForeground() : dayList.getOutOfMonthForeground());
                    setBackground(mix(dayList.getWeekendBackground(), withinMonth ? dayList.getBackground() : dayList.getOutOfMonthBackground()));
                } else {
                    if (withinMonth) {
                        setForeground(dayList.getForeground());
                        setBackground(dayList.getBackground());
                    } else {
                        setForeground(dayList.getOutOfMonthForeground());
                        setBackground(dayList.getOutOfMonthBackground());
                    }
                }
            }
            if (hasFocus) {
                setBackground(dayList.getSelectionBackground().brighter());
                setBorder(BorderFactory.createLineBorder(dayList.getSelectionBackground()));
            } else {
                setBorder(BorderFactory.createLineBorder(getBackground()));
            }
            return this;
        }
    }

    private static class WeekRenderer extends JLabel implements CalendarDayListCellRenderer {
        public Component getCalendarDayListCellRendererComponent(JCalendarDayList dayList, Object value, boolean isSelected, boolean hasFocus, boolean weekend, boolean withinMonth, int row, int column) {
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setHorizontalAlignment(SwingConstants.RIGHT);
            setText((String) value);
            setFont(dayList.getWeekFont());
            setOpaque(true);
            setEnabled(dayList.isEnabled());
            if (weekend) {
                setForeground(mix(dayList.getWeekendForeground(), dayList.getHeaderForeground()));
                setBackground(mix(dayList.getWeekendBackground(), dayList.getHeaderBackground()));
            } else {
                setForeground(dayList.getHeaderForeground());
                setBackground(dayList.getHeaderBackground());
            }
            return this;
        }

    }
}
