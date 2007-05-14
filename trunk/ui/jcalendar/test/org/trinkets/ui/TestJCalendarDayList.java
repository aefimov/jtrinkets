package org.trinkets.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Test form
 *
 * @author Alexey Efimov
 */
public class TestJCalendarDayList {
    private JCalendarDayList dayList;
    private JPanel root;
    private JSpinner year;
    private JComboBox month;

    public TestJCalendarDayList() {
        year.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Integer y = (Integer) year.getValue();
                Calendar base = dayList.getModel().getBaseValue();
                if (y != null && y >= base.getActualMinimum(Calendar.YEAR) && y <= base.getActualMaximum(Calendar.YEAR)) {
                    base.set(Calendar.YEAR, y);
                    dayList.setModel(new DefaultCalendarDayListModel(base));
                }
            }
        });
        month.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Calendar base = dayList.getModel().getBaseValue();
                int monthIndex = month.getSelectedIndex();
                if (monthIndex >= Calendar.JANUARY && monthIndex <= Calendar.DECEMBER) {
                    base.set(Calendar.MONTH, monthIndex);
                    dayList.setModel(new DefaultCalendarDayListModel(base));
                }
            }
        });
        Calendar base = dayList.getModel().getBaseValue();
        year.setValue(base.get(Calendar.YEAR));
        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] monthsNames = symbols.getMonths();
        String[] months = new String[12];
        System.arraycopy(monthsNames, 0, months, 0, months.length);
        month.setModel(new DefaultComboBoxModel(months));
        month.setSelectedIndex(base.get(Calendar.MONTH));
        dayList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Calendar calendar = dayList.getSelectedDate();
                if (calendar != null) {
                    JOptionPane.showMessageDialog(root, calendar.getTime().toString());
                }

            }
        });
    }

    public JPanel getRoot() {
        return root;
    }

}
