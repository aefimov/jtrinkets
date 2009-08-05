package org.trinkets.util.diff;

/**
 * Diff diff instruction "how to get from 'source' to 'target'".
 * Or what the difference between source and target.
 *
 * @author Alexey Efimov
 */
final class DiffInstruction {
    private final Type type;
    private int length;

    public DiffInstruction(Type type, int length) {
        this.length = length;
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public Type getType() {
        return type;
    }

    public void add(int length) {
        this.length += length;
    }

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
        REMOVED
    }
}
                             