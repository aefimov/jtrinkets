package org.trinkets.ui;

import org.trinkets.ui.plaf.BasicCalendarDayListUI;
import org.trinkets.ui.plaf.CalendarDayListUI;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

/**
 * UI component for {@link java.util.Calendar}.
 *
 * @author Alexey Efimov
 */
public class JCalendarDayList extends JComponent implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CalendarDayListUI";

    static {
        UIManager.put(uiClassID, BasicCalendarDayListUI.class.getName());
        // Colors
        UIManager.put("CalendarDayList.foreground", UIManager.getColor("Label.foreground"));
        UIManager.put("CalendarDayList.background", UIManager.getColor("Label.background"));
        UIManager.put("CalendarDayList.border", UIManager.getBorder("Label.border"));
        UIManager.put("CalendarDayList.selectionForeground", UIManager.getColor("List.selectionForeground"));
        UIManager.put("CalendarDayList.selectionBackground", UIManager.getColor("List.selectionBackground"));
        UIManager.put("CalendarDayList.headerForeground", UIManager.getColor("List.selectionForeground"));
        UIManager.put("CalendarDayList.headerBackground", UIManager.getColor("List.selectionBackground"));
        UIManager.put("CalendarDayList.weekendForeground", UIManager.getColor("Label.foreground").brighter().brighter());
        UIManager.put("CalendarDayList.weekendBackground", UIManager.getColor("Label.background").darker());
        UIManager.put("CalendarDayList.outOfMonthForeground", UIManager.getColor("Label.foreground").brighter().brighter().brighter());
        UIManager.put("CalendarDayList.outOfMonthBackground", UIManager.getColor("Label.background"));
        UIManager.put("CalendarDayList.dayFont", UIManager.getFont("Label.font"));
        UIManager.put("CalendarDayList.weekFont", UIManager.getFont("Label.font").deriveFont(Font.BOLD));
    }

    private CalendarDayListModel model;
    private CalendarDayListCellRenderer dayCellRenderer;
    private CalendarDayListCellRenderer weekCellRenderer;
    private Font dayFont;
    private Font weekFont;
    private Color weekendForeground;
    private Color weekendBackground;
    private Color headerForeground;
    private Color headerBackground;
    private Color selectionForeground;
    private Color selectionBackground;
    private Color outOfMonthForeground;
    private Color outOfMonthBackground;

    public JCalendarDayList() {
        updateUI();
    }

    /**
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI(UIManager.getUI(this));
    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the CalendarDayListUI object that renders this component
     */
    public CalendarDayListUI getUI() {
        return (CalendarDayListUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui the CalendarDayListUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(CalendarDayListUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "CalendarDayListUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * See readObject() and writeObject() in JComponent for more
     * information about serialization in Swing.
     *
     * @param s Stream
     * @throws java.io.IOException if serialization error
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = getWriteObjCounter(this);
            setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }

    /**
     * Swing hack for access to package level methods of JComponent.
     *
     * @param comp Component
     * @return Counter
     * @see javax.swing.JComponent#getWriteObjCounter(javax.swing.JComponent)
     */
    private static byte getWriteObjCounter(JComponent comp) {
        try {
            Method method = JComponent.class.getMethod("getWriteObjCounter", JComponent.class);
            method.setAccessible(true);
            return ((Byte) method.invoke(null, comp));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Swing hack for access to package level methods of JComponent.
     *
     * @param comp  Component
     * @param count Counter
     * @see javax.swing.JComponent#setWriteObjCounter(javax.swing.JComponent,byte)
     */
    private static void setWriteObjCounter(JComponent comp, byte count) {
        try {
            Method method = JComponent.class.getMethod("setWriteObjCounter", JComponent.class, Byte.class);
            method.setAccessible(true);
            method.invoke(null, comp, count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a string representation of this JCalendarDayList. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return a string representation of this JCalendarDayList.
     */
    protected String paramString() {
        return super.paramString();
    }

    public CalendarDayListModel getModel() {
        return model;
    }

    public void setModel(CalendarDayListModel model) {
        CalendarDayListModel oldValue = this.model;
        this.model = model;
        firePropertyChange("model", oldValue, this.model);
    }

    public CalendarDayListCellRenderer getDayCellRenderer() {
        return dayCellRenderer;
    }

    public void setDayCellRenderer(CalendarDayListCellRenderer cellRenderer) {
        CalendarDayListCellRenderer oldValue = this.dayCellRenderer;
        this.dayCellRenderer = cellRenderer;
        firePropertyChange("dayCellRenderer", oldValue, this.dayCellRenderer);
    }

    public CalendarDayListCellRenderer getWeekCellRenderer() {
        return weekCellRenderer;
    }

    public void setWeekCellRenderer(CalendarDayListCellRenderer cellRenderer) {
        CalendarDayListCellRenderer oldValue = this.weekCellRenderer;
        this.weekCellRenderer = cellRenderer;
        firePropertyChange("weekCellRenderer", oldValue, this.weekCellRenderer);
    }

    public Font getDayFont() {
        return dayFont;
    }

    public void setDayFont(Font dayFont) {
        Font oldValue = this.dayFont;
        this.dayFont = dayFont;
        firePropertyChange("dayFont", oldValue, this.dayFont);
    }

    public Font getWeekFont() {
        return weekFont;
    }

    public void setWeekFont(Font weekFont) {
        Font oldValue = this.weekFont;
        this.weekFont = weekFont;
        firePropertyChange("weekFont", oldValue, this.weekFont);
    }

    public Color getWeekendForeground() {
        return weekendForeground;
    }

    public void setWeekendForeground(Color weekendForeground) {
        Color oldValue = this.weekendForeground;
        this.weekendForeground = weekendForeground;
        firePropertyChange("weekendForeground", oldValue, this.weekendForeground);
    }

    public Color getWeekendBackground() {
        return weekendBackground;
    }

    public void setWeekendBackground(Color weekendBackground) {
        Color oldValue = this.weekendBackground;
        this.weekendBackground = weekendBackground;
        firePropertyChange("weekendBackground", oldValue, this.weekendBackground);
    }

    public Color getHeaderForeground() {
        return headerForeground;
    }

    public void setHeaderForeground(Color headerForeground) {
        Color oldValue = this.headerForeground;
        this.headerForeground = headerForeground;
        firePropertyChange("headerForeground", oldValue, this.headerForeground);
    }

    public Color getHeaderBackground() {
        return headerBackground;
    }

    public void setHeaderBackground(Color headerBackground) {
        Color oldValue = this.headerBackground;
        this.headerBackground = headerBackground;
        firePropertyChange("headerBackground", oldValue, this.headerBackground);
    }

    public Color getSelectionForeground() {
        return selectionForeground;
    }

    public void setSelectionForeground(Color selectionForeground) {
        Color oldValue = this.selectionForeground;
        this.selectionForeground = selectionForeground;
        firePropertyChange("selectionForeground", oldValue, this.selectionForeground);
    }

    public Color getSelectionBackground() {
        return selectionBackground;
    }

    public void setSelectionBackground(Color selectionBackground) {
        Color oldValue = this.selectionBackground;
        this.selectionBackground = selectionBackground;
        firePropertyChange("selectionBackground", oldValue, this.selectionBackground);
    }

    public Color getOutOfMonthForeground() {
        return outOfMonthForeground;
    }

    public void setOutOfMonthForeground(Color OutOfMonthForeground) {
        Color oldValue = this.outOfMonthForeground;
        this.outOfMonthForeground = OutOfMonthForeground;
        firePropertyChange("outOfMonthForeground", oldValue, this.outOfMonthForeground);
    }

    public Color getOutOfMonthBackground() {
        return outOfMonthBackground;
    }

    public void setOutOfMonthBackground(Color outOfMonthBackground) {
        Color oldValue = this.outOfMonthBackground;
        this.outOfMonthBackground = outOfMonthBackground;
        firePropertyChange("outOfMonthBackground", oldValue, this.outOfMonthBackground);
    }
}
