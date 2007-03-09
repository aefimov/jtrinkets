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
        dayList.setModel(new DefaultCalendarDayListModel(new Locale("en")));
        dayList.setEnabled(false);
        frame.getContentPane().add(dayList);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
