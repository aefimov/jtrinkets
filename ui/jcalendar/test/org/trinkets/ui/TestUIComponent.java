package org.trinkets.ui;

import org.trinkets.ui.plaf.DefaultCalendarDayListModel;

import javax.swing.*;
import java.util.Calendar;
import java.util.Locale;

/**
 * Test of UI component.
 *
 * @author Alexey Efimov
 */
public class TestUIComponent {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        spinnerDateModel.setCalendarField(Calendar.MONTH);
        JCalendarDayList dayList = new JCalendarDayList();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2007, Calendar.JANUARY, 1);
        dayList.setModel(new DefaultCalendarDayListModel(calendar, new Locale("ru")));
        calendar.set(2006, Calendar.DECEMBER, 28);
        dayList.setSelectedDate(calendar);
        frame.getContentPane().add(dayList);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
