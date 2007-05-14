package org.trinkets.ui.plaf;

import org.trinkets.ui.CalendarDayListModel;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DefaultCalendarDayListModel implements CalendarDayListModel {
    private final Calendar calendar;
    private final String[] weekdays;
    private final SimpleDateFormat format;

    public DefaultCalendarDayListModel(Calendar calendar, DateFormatSymbols symbols) {
        this.calendar = (Calendar) calendar.clone();
        String[] shortWeekdays = symbols.getShortWeekdays();
        weekdays = new String[shortWeekdays.length - 1];
        for (int i = 0; i < shortWeekdays.length - 1; i++) {
            String weekday = shortWeekdays[i + 1];
            weekdays[i] = weekday;
        }
        format = new SimpleDateFormat("EEEE, dd MMM yyyy", symbols);
    }

    public DefaultCalendarDayListModel(Calendar calendar, Locale locale) {
        this(calendar, new DateFormatSymbols(locale));
    }

    public DefaultCalendarDayListModel(Calendar calendar) {
        this(calendar, Locale.getDefault());
    }

    public DefaultCalendarDayListModel(TimeZone tz, Locale locale) {
        this(Calendar.getInstance(tz, locale), locale);
    }

    public DefaultCalendarDayListModel(TimeZone tz) {
        this(tz, Locale.getDefault());
    }

    public DefaultCalendarDayListModel(Locale locale) {
        this(TimeZone.getDefault(), locale);
    }

    public DefaultCalendarDayListModel() {
        this(TimeZone.getDefault(), Locale.getDefault());
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

    public String getToolTipTextAt(int weekIndex, int weekDayIndex) {
        synchronized (format) {
            return format.format(getValueAt(weekIndex, weekDayIndex).getTime());
        }
    }

    public int weekIndexOf(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();
        int weekIndex = calendar.get(Calendar.WEEK_OF_YEAR);
        int modelYear = this.calendar.get(Calendar.YEAR);
        int dy = c.get(Calendar.YEAR) < modelYear ? 1 : (c.get(Calendar.YEAR) > modelYear) ? -1 : 0;
        while (c.get(Calendar.YEAR) != modelYear) {
            weekIndex += (-dy) * c.getActualMaximum(Calendar.WEEK_OF_YEAR);
            c.add(Calendar.YEAR, dy);
        }

        return weekIndex - this.calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int weekDayIndexOf(Calendar calendar) {
        int delta = this.calendar.getFirstDayOfWeek() - Calendar.SUNDAY;
        return calendar.get(Calendar.DAY_OF_WEEK) - 1 - delta;
    }
}
