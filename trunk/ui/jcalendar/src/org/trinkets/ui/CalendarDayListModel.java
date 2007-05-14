package org.trinkets.ui;

import java.util.Calendar;

/**
 * Model for JCalendarDayList.
 *
 * @author Alexey Efimov
 */
public interface CalendarDayListModel {
    /**
     * Return representation of day date for week relative to current month.
     *
     * @param weekIndex    week index from <code>-1</code> to <code>getWeeksInMonth() + 1</code>.
     * @param weekDayIndex Number of week day (from <code>0</code>).
     * @return String of day date - 1, 2 .. 31.
     */
    String getDayCellValue(int weekIndex, int weekDayIndex);

    /**
     * Return names of header line - week names.
     *
     * @param weekDayIndex Number of week day.
     * @return Name of day in week
     */
    String getWeekCellValue(int weekDayIndex);

    /**
     * Return count of weeks in month.
     *
     * @return Number of weeks in month
     */
    int getWeeksInMonth();

    /**
     * Return count of days within week.
     *
     * @return Number of days in week
     */
    int getDaysInWeek();

    /**
     * Return <code>true</code> if cell is a weekend.
     *
     * @param weekDayIndex Number of week day (from <code>0</code>).
     * @return <code>true</code> if cell is a weekend.
     */
    boolean isWeekendCell(int weekDayIndex);

    /**
     * Return <code>true</code> if cell within current month.
     *
     * @param weekIndex    week index from <code>-1</code> to <code>getWeeksInMonth() + 1</code>.
     * @param weekDayIndex Number of week day (from <code>0</code>).
     * @return <code>true</code> if cell within current month.
     */
    boolean isWithinMonthCell(int weekIndex, int weekDayIndex);

    /**
     * Return Calendar value of cell.
     *
     * @param weekIndex    week index from <code>-1</code> to <code>getWeeksInMonth() + 1</code>.
     * @param weekDayIndex Number of week day (from <code>0</code>).
     * @return Calendar value of cell.
     */
    Calendar getValueAt(int weekIndex, int weekDayIndex);

    /**
     * Return tooltip for value of cell.
     *
     * @param weekIndex    week index from <code>-1</code> to <code>getWeeksInMonth() + 1</code>.
     * @param weekDayIndex Number of week day (from <code>0</code>).
     * @return Tooltip for value of cell.
     */
    String getToolTipTextAt(int weekIndex, int weekDayIndex);

    /**
     * Return week index of date
     *
     * @param calendar Calendar
     * @return Week index from <code>-1</code> to <code>getWeeksInMonth() + 1</code>.
     */
    int weekIndexOf(Calendar calendar);

    /**
     * Return week day index of date
     *
     * @param calendar Calendar
     * @return Week day index from <code>0</code>
     */
    int weekDayIndexOf(Calendar calendar);
}
