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
        if (opposite != null && opposite.getOpposite() != this) {
            opposite.setOpposite(this);
        }
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
        return hasPrevious() ? previous.getFirst() : this;
    }

    public DiffNode getLast() {
        return hasNext() ? next.getLast() : this;
    }

    public int getOffset() {
        return hasPrevious() ? previous.getOffset() + previous.getLength() : 0;
    }

    public void insertBefore(DiffNode node) {
        DiffNode last = node.getLast();

        if (hasPrevious()) {
            previous.setNext(node);
        }
        node.setPrevious(previous);
        last.setNext(this);
        setPrevious(last);

        if (node.hasOpposite()) {
            DiffNode oppositeNode = node.getOpposite();
            DiffNode oppositeLast = oppositeNode.getLast();
            if (hasOpposite()) {
                if (opposite.hasPrevious()) {
                    opposite.getPrevious().setNext(oppositeNode);
                }
                oppositeNode.setPrevious(opposite.getPrevious());
                oppositeLast.setNext(opposite);
                opposite.setPrevious(oppositeLast);
            } else {
                linkOpposite(node);
            }
        }
    }

    public void insertAfter(DiffNode node) {
        DiffNode last = node.getLast();

        if (hasNext()) {
            next.setPrevious(last);
        }
        last.setNext(next);
        node.setPrevious(this);
        setNext(node);

        if (node.hasOpposite()) {
            DiffNode oppositeNode = node.getOpposite();
            DiffNode oppositeLast = oppositeNode.getLast();
            if (hasOpposite()) {
                if (opposite.hasNext()) {
                    opposite.getNext().setPrevious(oppositeLast);
                }
                oppositeLast.setNext(opposite.getNext());
                oppositeNode.setPrevious(opposite);
                opposite.setNext(oppositeNode);
            } else {
                linkOpposite(node);
            }
        }
    }

    private void linkOpposite(DiffNode node) {
        if (node.hasOpposite()) {
            if (node.hasPrevious() && node.getPrevious().hasOpposite()) {
                node.getOpposite().setPrevious(node.getPrevious().getOpposite());
            } else {
                node.getOpposite().setPrevious(node.getPrevious());
            }
            if (node.hasNext() && node.getNext().hasOpposite()) {
                node.getOpposite().setNext(node.getNext().getOpposite());
            } else {
                node.getOpposite().setNext(node.getNext());
            }
        }
    }

    public DiffNode remove() {
        if (hasOpposite()) {
            opposite.setOpposite(null);
            opposite.remove();
        }
        if (hasPrevious()) {
            previous.setNext(next);
        }
        if (hasNext()) {
            next.setPrevious(previous);
        }
        return hasPrevious() ? previous : next;
    }

    public DiffNode splitByLength(int l) {
        int oldLength = DiffType.VIRTUAL.equals(type) ? getOpposite().getLength() : length;
        int newLength = DiffType.VIRTUAL.equals(type) ? 0 : l;

        DiffType oppositeType = hasOpposite() ? opposite.getType() : null;
        int oppositeLength = DiffType.VIRTUAL.equals(oppositeType) ? 0 : l;

        for (int i = 0; i < oldLength / l; i++) {
            DiffNode newNode = new DiffNode(type, newLength);
            if (oppositeType != null) {
                newNode.setOpposite(new DiffNode(oppositeType, oppositeLength));
            }
            insertBefore(newNode);
        }

        return remove();
    }

    public DiffNode getLeft() {
        if (hasOpposite()) {
            if (DiffType.REMOVED.equals(getOpposite().getType()) ||
                DiffType.ADDED.equals(type)) {
                return opposite;
            }
        }
        return this;
    }
}
