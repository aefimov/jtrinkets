package org.trinkets.util.diff;

/**
 * DiffMarker width statistics counters.
 *
 * @author Alexey Efimov
 */
public class StatisticsDiffMarker<T> implements DiffMarker<T> {
    protected final StringBuilderDiffMarker<T> delegate;

    private int addedLength = 0;
    private int addedCount = 0;
    private int removedLength = 0;
    private int removedCount = 0;
    private int unchangedLength = 0;
    private int unchangedCount = 0;

    protected StatisticsDiffMarker(StringBuilderDiffMarker<T> delegate) {
        this.delegate = delegate;
    }

    public void apply(DiffNode sourceNode, T[] source, int sourceOffset, int sourceLength,
                      DiffNode targetNode, T[] target, int targetOffset, int targetLength) {

        update(
            sourceNode.getType(), delegate.toCharSequence(source, sourceOffset, sourceLength),
            targetNode.getType(), delegate.toCharSequence(target, targetOffset, targetLength)
        );
        delegate.apply(sourceNode, source, sourceOffset, sourceLength, targetNode, target, targetOffset, targetLength);
    }

    public void reset() {
        addedLength = 0;
        addedCount = 0;
        removedLength = 0;
        removedCount = 0;
        unchangedLength = 0;
        unchangedCount = 0;
        if (delegate != null) {
            delegate.reset();
        }
    }

    private void update(DiffNode.Type sourceType, CharSequence source, DiffNode.Type targetType, CharSequence target) {
        if (DiffNode.Type.ADDED.equals(targetType)) {
            addedCount++;
            addedLength += target.length();
        }

        if (DiffNode.Type.REMOVED.equals(sourceType)) {
            removedCount++;
            removedLength += source.length();
        }

        if (DiffNode.Type.UNCHANGED.equals(sourceType)) {
            unchangedCount++;
            unchangedLength += source.length();
        }
    }

    public double getAddedPercent() {
        return addedLength > 0 ? (unchangedLength > 0 ? ((double) addedLength) / ((double) (addedLength + unchangedLength)) : 1) : 0;
    }

    public double getRemovePercent() {
        return removedLength > 0 ? (unchangedLength > 0 ? ((double) removedLength) / ((double) (removedLength + unchangedLength)) : 1) : 0;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public int getUnchangedCount() {
        return unchangedCount;
    }
}
