package org.trinkets.util.diff;

/**
 * DiffMarker width statistics counters.
 *
 * @author Alexey Efimov
 */
public class StatisticsDiffMarker<T> implements DiffMarker<T> {
    protected final DiffMarker<T> delegate;

    private int added = 0;
    private int removed = 0;
    private int unchanged = 0;

    protected StatisticsDiffMarker(DiffMarker<T> delegate) {
        this.delegate = delegate;
    }

    public void apply(DiffNode.Type sourceType, T[] source, int sourceOffset, int sourceLength,
                      DiffNode.Type targetType, T[] target, int targetOffset, int targetLength) {
        update(sourceType, sourceLength, targetType, targetLength);
        if (delegate != null) {
            delegate.apply(sourceType, source, sourceOffset, sourceLength, targetType, target, targetOffset, targetLength);
        }
    }

    public void reset() {
        added = 0;
        removed = 0;
        unchanged = 0;
        if (delegate != null) {
            delegate.reset();
        }
    }

    private void update(DiffNode.Type sourceType, int sourceLength, DiffNode.Type targetType, int targetLength) {
        if (DiffNode.Type.ADDED.equals(targetType)) {
            added += targetLength;
        }

        if (DiffNode.Type.REMOVED.equals(sourceType)) {
            removed += sourceLength;
        }

        if (DiffNode.Type.UNCHANGED.equals(sourceType)) {
            unchanged += sourceLength;
        }
    }

    public double getAddedPercent() {
        return added > 0 ? (unchanged > 0 ? ((double) added) / ((double) (added + unchanged)) : 1) : 0;
    }

    public double getRemovePercent() {
        return removed > 0 ? (unchanged > 0 ? ((double) removed) / ((double) (removed + unchanged)) : 1) : 0;
    }
}
