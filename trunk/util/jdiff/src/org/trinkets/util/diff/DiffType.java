package org.trinkets.util.diff;

/**
 * Diff type.
 *
 * @author Alexey Efimov
 */
public enum DiffType {
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

    public static boolean isChanged(DiffType type) {
        return !UNCHANGED.equals(type) && !VIRTUAL.equals(type);
    }
}
