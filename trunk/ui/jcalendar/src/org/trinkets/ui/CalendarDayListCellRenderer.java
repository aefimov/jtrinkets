package org.trinkets.ui;

import java.awt.*;

/**
 * Renderer for cells in {@link org.trinkets.ui.JCalendarDayList}.
 *
 * @author Alexey Efimov
 */
public interface CalendarDayListCellRenderer {
    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     *
     * @param dayList     the <code>JCalendarDayList</code> that is asking
     *                    the renderer to draw; can be <code>null</code>
     * @param value       the value of the cell to be rendered.  It is up
     *                    to the specific renderer to interpret and draw
     *                    the value.  For example, if  <code>value</code>
     *                    is the string "true", it could be rendered as a
     *                    string or it could be rendered as a check
     *                    box that is checked.  <code>null</code> is a
     *                    valid value
     * @param isSelected  true if the cell is to be rendered with the
     *                    selection highlighted; otherwise false
     * @param hasFocus    if true, render cell appropriately.  For
     *                    example, put a special border on the cell, if
     *                    the cell can be edited, render in the color used
     *                    to indicate editing
     * @param weekend     Flag to show what curent cell is weekend cell
     * @param withinMonth Flag to show what cell is winin curent month
     * @param row         the row index of the cell being drawn.  When
     *                    drawing the header, the value of
     *                    <code>row</code> is -1
     * @param column      the column index of the cell being drawn @return Renderer component
     * @return Renderer component
     */
    Component getCalendarDayListCellRendererComponent(JCalendarDayList dayList,
                                                      Object value,
                                                      boolean isSelected,
                                                      boolean hasFocus,
                                                      boolean weekend,
                                                      boolean withinMonth, int row, int column);
}
