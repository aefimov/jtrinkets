package org.trinkets.ui.plaf;

import org.trinkets.ui.CalendarDayListModel;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class DefaultCalendarDayListModel implements CalendarDayListModel {
    private final Calendar calendar;
    private final String[] weekdays;

    public DefaultCalendarDayListModel(Locale locale) {
        calendar = Calendar.getInstance(locale);
        String[] shortWeekdays = new DateFormatSymbols(locale).getShortWeekdays();
        weekdays = new String[shortWeekdays.length - 1];
        for (int i = 0; i < shortWeekdays.length - 1; i++) {
            String weekday = shortWeekdays[i + 1];
            weekdays[i] = weekday;
        }
    }

    public DefaultCalendarDayListModel() {
        this(Locale.getDefault());
    }

    public String getDayCellValue(int weekIndex, int weekDayIndex) {
        Calendar c = getValueAt(weekIndex, weekDayIndex);
        return String.valueOf(c.get(Calendar.DATE));
    }

    public String getWeekCellValue(int weekDayIndex) {
        int delta = calendar.getFirstDayOfWeek() - Calendar.SUNDAY;
        int index = weekDayIndex + delta;
        return weekdays[index >= weekdays.length ? index - weekdays.length : index];
    }

    public int getWeeksInMonth() {
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    public int getDaysInWeek() {
        return calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
    }

    public boolean isWeekendCell(int weekDayIndex) {
        int delta = calendar.getFirstDayOfWeek() - Calendar.SUNDAY;
        int index = weekDayIndex + delta;
        int day = (index >= weekdays.length ? index - weekdays.length : index) + 1;
        return Calendar.SUNDAY == day || Calendar.SATURDAY == day;
    }

    public boolean isWithinMonthCell(int weekIndex, int weekDayIndex) {
        Calendar c = getValueAt(weekIndex, weekDayIndex);
        return c.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
    }

    /**
     * Return delta for first day of week. For some locales week is started with Sunday, for some - with Monday
     *
     * @param c Calendar
     * @return Delta is days.
     */
    private int getStartingDaySpacer(Calendar c) {
        c.set(Calendar.DATE, 1);
        int monthFirstDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = c.getFirstDayOfWeek();
        return firstDayOfWeek > monthFirstDayOfWeek ? getDaysInWeek() - firstDayOfWeek + monthFirstDayOfWeek : monthFirstDayOfWeek - firstDayOfWeek;
    }

    public Calendar getValueAt(int weekIndex, int weekDayIndex) {
        Calendar clone = (Calendar) calendar.clone();
        int dayIndex = weekIndex * getDaysInWeek() + weekDayIndex - getStartingDaySpacer(clone);
        clone.set(Calendar.DATE, 1);
        clone.add(Calendar.DATE, dayIndex);
        return clone;
    }
}
