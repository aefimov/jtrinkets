package org.trinkets.util.diff;

/**
 * Incremental marker.
 *
 * @author Alexey Efimov
 */
public abstract class IncrementalDiffMarker<T> extends StringDiffMarker {
    private final double threshold;
    private final StringBuilderDiffMarker<T> subMarker;
    private final StatisticsDiffMarker<T> statsMarker;

    protected IncrementalDiffMarker(StringBuilderDiffMarker<T> subMarker, double threshold) {
        this.threshold = threshold;
        this.subMarker = subMarker;
        this.statsMarker = new StatisticsDiffMarker<T>(subMarker);
    }

    @Override
    protected void apply(DiffNode.Type sourceType, CharSequence source, DiffNode.Type targetType, CharSequence target) {
        if (DiffNode.Type.REMOVED.equals(sourceType) && targetType.equals(DiffNode.Type.ADDED)) {
            statsMarker.reset();
            subCompare(source, target, statsMarker);
            if (statsMarker.getAddedPercent() < threshold && statsMarker.getRemovePercent() < threshold) {
                beforeSubMarkupText(sourceType, targetType);
                sourceResult.append(subMarker.getSourceResult());
                targetResult.append(subMarker.getTargetResult());
                afterSubMarkupText(sourceType, targetType);
            } else {
                super.apply(sourceType, source, targetType, target);
            }
        } else {
            super.apply(sourceType, source, targetType, target);
        }
    }

    protected void beforeSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
    }

    protected void afterSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
    }

    protected abstract void subCompare(CharSequence source, CharSequence target, DiffMarker<T> subMarker);
}