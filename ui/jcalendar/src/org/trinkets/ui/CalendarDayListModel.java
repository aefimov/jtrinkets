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
     * @param weekIndex    week index from -1 to getWeeksInMonth() + 1.
     * @param weekDayIndex Number of week day.
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
     * @param weekDayIndex Number of week day.
     * @return <code>true</code> if cell is a weekend.
     */
    boolean isWeekendCell(int weekDayIndex);

    /**
     * Return <code>true</code> if cell within current month.
     *
     * @param weekIndex    week index from -1 to getWeeksInMonth() + 1.
     * @param weekDayIndex Number of week day.
     * @return <code>true</code> if cell within current month.
     */
    boolean isWithinMonthCell(int weekIndex, int weekDayIndex);

    /**
     * Return Calendar value of cell.
     *
     * @param weekIndex    week index from -1 to getWeeksInMonth() + 1.
     * @param weekDayIndex Number of week day.
     * @return Calendar value of cell.
     */
    Calendar getValueAt(int weekIndex, int weekDayIndex);
}
