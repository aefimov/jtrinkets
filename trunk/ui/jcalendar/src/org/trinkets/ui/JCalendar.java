package org.trinkets.ui;

import org.trinkets.ui.plaf.CalendarUI;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * UI component for {@link java.util.Calendar}.
 *
 * @author Alexey Efimov
 */
public class JCalendar extends JComponent implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CalendarUI";

    private Calendar value;
    
    public JCalendar() {
        this(Calendar.getInstance());
    }

    public JCalendar(Calendar value) {
        this.value = value;
        updateUI();
    }

    public Calendar getValue() {
        return value;
    }
    
    public void setValue(Calendar value) {
        Calendar oldValue = this.value;
        this.value = value != null ? (Calendar) value.clone() : null;
        if (oldValue == null && this.value != null ||
            oldValue != null && !oldValue.equals(this.value)) {
            firePropertyChange("value", oldValue, this.value);
        }
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
     * @return the CalendarUI object that renders this component
     */
    public CalendarUI getUI() {
        return (CalendarUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui the CalendarUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(CalendarUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class
     * that renders this component.
     *
     * @return "CalendarUI"
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
     * Returns a string representation of this JCalendar. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return a string representation of this JCalendar.
     */
    protected String paramString() {
        return super.paramString();
    }

}
