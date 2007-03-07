package org.trinkets.ui;

/**
 * Model for JCalendar.
 *
 * @author Alexey Efimov
 */
public interface CalendarModel {
    int getYear();

    int getMonth();

    int getDay();

    int getHour();

    int getMinute();

    int getSecond();

    void setYear(int year);

    void setMonth(int month);

    void setDay(int day);

    void setHour(int hour);

    void setMinute(int minute);

    void setSecond(int second);
}
