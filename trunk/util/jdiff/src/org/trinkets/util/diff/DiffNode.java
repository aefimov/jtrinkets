package org.trinkets.util.diff;

/**
 * Diff result structure.
 *
 * @author Alexey Efimov
 */
public class DiffNode {
    private DiffType type;
    private int length;

    /**
     * Previous 'line'
     */
    private DiffNode previous = null;

    /**
     * Next 'line'
     */
    private DiffNode next = null;

    /**
     * Opposite change 'content'. For example from REMOVED to ADDED in same position.
     */
    private DiffNode opposite = null;

    public DiffNode() {
    }

    public DiffNode(DiffType type, int length) {
        setType(type);
        setLength(length);
    }

    public DiffNode getPrevious() {
        return previous;
    }

    public void setPrevious(DiffNode previous) {
        this.previous = previous;
    }

    public DiffNode getNext() {
        return next;
    }

    public void setNext(DiffNode next) {
        this.next = next;
    }

    public DiffNode getOpposite() {
        return opposite;
    }

    public void setOpposite(DiffNode opposite) {
        this.opposite = opposite;
    }

    public DiffType getType() {
        return type;
    }

    public void setType(DiffType type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean hasNext() {
        return next != null && next != this;
    }

    public boolean hasPrevious() {
        return previous != null && previous != this;
    }

    public boolean hasOpposite() {
        return opposite != null && opposite != this;
    }


    public DiffNode getFirst() {
        DiffNode first = hasPrevious() ? previous.getFirst() : this;
        return first.hasOpposite() && DiffType.ADDED.equals(first.getType()) ? first.getOpposite() : first;
    }
}
