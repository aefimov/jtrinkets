package org.trinkets.util.diff;

/**
 * DiffMarker width statistics counters.
 *
 * @author Alexey Efimov
 */
public abstract class StatisticsDiffMarker<T> implements DiffMarker<T> {
    public int added;
    public int removed;
    public int unchanged;

    public final void apply(DiffType sourceType, T[] source, int sourceOffset, int sourceLength,
                      DiffType targetType, T[] target, int targetOffset, int targetLength) {
        update(sourceType, sourceLength, targetType, targetLength);
        markup(sourceType, source, sourceOffset, sourceLength, targetType, target, targetOffset, targetLength);
    }

    private void update(DiffType sourceType, int sourceLength, DiffType targetType, int targetLength) {
        if (DiffType.ADDED.equals(targetType)) {
            added += targetLength;
        }

        if (DiffType.REMOVED.equals(sourceType)) {
            removed += sourceLength;
        }

        if (DiffType.UNCHANGED.equals(sourceType)) {
            unchanged += sourceLength;
        }
    }

    public double getAddedPercent() {
        return added > 0 ? (unchanged > 0 ? ((double) added) / ((double) (added + unchanged)) : 1) : 0;
    }

    public double getRemovePercent() {
        return removed > 0 ? (unchanged > 0 ? ((double) removed) / ((double) (removed + unchanged)) : 1) : 0;
    }

    protected abstract void markup(DiffType sourceType, T[] source, int sourceOffset, int sourceLength,
                                   DiffType targetType, T[] target, int targetOffset, int targetLength);
}
