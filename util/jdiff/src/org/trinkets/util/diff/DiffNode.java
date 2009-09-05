package org.trinkets.util.diff;

import java.util.ArrayList;
import java.util.List;

/**
 * Diff result structure.
 *
 * @author Alexey Efimov
 */
public class DiffNode {
    private Type type;
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

    public DiffNode(Type type, int length) {
        setType(type);
        setLength(length);
    }

    public DiffNode getPrevious() {
        return previous;
    }

    public void setPrevious(DiffNode previous) {
        this.previous = previous;
        if (previous != null && previous.getNext() != this) {
            previous.next = this;
        }
    }

    public DiffNode getNext() {
        return next;
    }

    public void setNext(DiffNode next) {
        this.next = next;
        if (next != null && next.getPrevious() != this) {
            next.previous = this;
        }
    }

    public DiffNode getOpposite() {
        return opposite;
    }

    public void setOpposite(DiffNode opposite) {
        this.opposite = opposite;
        if (opposite != null && opposite.getOpposite() != this) {
            opposite.opposite = this;
        }
        if (hasPrevious() && previous.hasOpposite()) {
            previous.getOpposite().setNext(opposite);
        } else if (opposite != null) {
            opposite.setPrevious(null);
        }
    }

    public void insertBefore(DiffNode node) {
        DiffNode last = node.getLast();

        if (hasPrevious()) {
            previous.setNext(node);
        }
        node.setPrevious(previous);
        last.setNext(this);
        setPrevious(last);
    }

    public void insertAfter(DiffNode node) {
        DiffNode last = node.getLast();

        if (hasNext()) {
            next.setPrevious(last);
        }
        last.setNext(next);
        node.setPrevious(this);
        setNext(node);
    }

    public DiffNode remove() {
        if (hasOpposite()) {
            opposite.opposite = null;
            opposite.remove();
        }
        if (hasPrevious()) {
            previous.setNext(next);
        }
        if (hasNext()) {
            next.setPrevious(previous);
        }
        DiffNode placeholder = hasPrevious() ? previous : this.next;
        this.previous = null;
        this.next = null;
        return placeholder;
    }

    public DiffNode splitByLength(int l) {
        int oldLength = Type.VIRTUAL.equals(type) ? getOpposite().getLength() : length;
        int newLength = Type.VIRTUAL.equals(type) ? 0 : l;

        Type oppositeType = hasOpposite() ? opposite.getType() : null;
        int oppositeLength = Type.VIRTUAL.equals(oppositeType) ? 0 : l;

        for (int i = 0; i < oldLength / l; i++) {
            DiffNode newNode = new DiffNode(type, newLength);
            if (oppositeType != null) {
                newNode.setOpposite(new DiffNode(oppositeType, oppositeLength));
            }
            insertBefore(newNode);
            if (hasOpposite() && newNode.hasOpposite()) {
                opposite.insertBefore(newNode.getOpposite());
            }
        }

        return remove();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
        return hasPrevious() ? previous.getFirst() : this;
    }

    public DiffNode getLast() {
        return hasNext() ? next.getLast() : this;
    }

    public int getOffset() {
        return hasPrevious() ? previous.getOffset() + previous.getLength() : 0;
    }

    @Override
    public String toString() {
        return toStringWithoutOpposite() + (hasOpposite() ? "->" + opposite.toStringWithoutOpposite() : "");
    }

    private String toStringWithoutOpposite() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.name().charAt(0));
        builder.append(":");
        builder.append(getOffset());
        builder.append(",");
        builder.append(length);
        return builder.toString();
    }

    public DiffNode[] toArray() {
        List<DiffNode> nodes = new ArrayList<DiffNode>();
        DiffNode current = this;
        while (current != null) {
            nodes.add(current);
            current = current.getNext();
        }
        return nodes.toArray(new DiffNode[nodes.size()]);
    }

    public DiffNode reverse() {
        DiffNode current = getFirst();
        while (current != null) {
            DiffNode next = current.getNext();
            DiffNode previous = current.previous;
            current.previous = next;
            current.next = previous;
            current = next;
        }
        return getFirst();
    }

    /**
     * Diff type.
     *
     * @author Alexey Efimov
     */
    public static enum Type {
        /**
         * This instruction type is for ranges in 'source' or 'target' array is not changed and
         * can be copied from anywhere (source or target).
         */
        UNCHANGED,

        /**
         * This instruction type is for ranges added to 'target' array and can be copied only
         * from 'target'.
         */
        ADDED,

        /**
         * This instruction type is for ranged removed from 'source' and can be copied only
         * from 'source'.
         */
        REMOVED,

        /**
         * Fake instruction type to fill opposite arrays. For example if 'source' have {@link #REMOVED} instruction but
         * have not corresponding {@link #ADDED} instruction in 'target', then target's array will modified by addition
         * this instruction.
         */
        VIRTUAL;

        public boolean isChanged() {
            return equals(ADDED) || equals(REMOVED);
        }
    }
}
