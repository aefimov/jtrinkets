package org.trinkets.ui;

import javax.swing.*;

/**
 * Test of UI component.
 *
 * @author Alexey Efimov
 */
public class TestUIComponent {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        TestJCalendarDayList dayList = new TestJCalendarDayList();
        frame.getContentPane().add(dayList.getRoot());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
