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
        this(subMarker, threshold, subMarker.decorator);
    }

    protected IncrementalDiffMarker(StringBuilderDiffMarker<T> subMarker, double threshold, StringBuilderDiffMarkupDecorator decorator) {
        super(decorator);
        this.threshold = threshold;
        this.subMarker = subMarker;
        this.statsMarker = new StatisticsDiffMarker<T>(subMarker);
    }

    @Override
    protected void apply(DiffNode sourceNode, CharSequence source, DiffNode targetNode, CharSequence target) {
        if (DiffNode.Type.REMOVED.equals(sourceNode.getType()) && DiffNode.Type.ADDED.equals(targetNode.getType())) {
            statsMarker.reset();
            subCompare(source, target, statsMarker);
            if ((statsMarker.getAddedCount() > 0 || statsMarker.getRemovedCount() > 0) && statsMarker.getUnchangedCount() > 0 &&
                (statsMarker.getAddedCount() < 2 && statsMarker.getRemovedCount() < 2 || statsMarker.getAddedPercent() < threshold && statsMarker.getRemovePercent() < threshold)) {
                beforeSubMarkupText(sourceNode, targetNode);
                sourceResult.append(subMarker.getSourceResult());
                targetResult.append(subMarker.getTargetResult());
                afterSubMarkupText(sourceNode, targetNode);
            } else {
                super.apply(sourceNode, source, targetNode, target);
            }
        } else {
            super.apply(sourceNode, source, targetNode, target);
        }
    }

    protected void beforeSubMarkupText(DiffNode sourceNode, DiffNode targetNode) {
        if (decorator != null) {
            decorator.beforeSubMarkupText(sourceNode, sourceResult, targetNode, targetResult);
        }
    }

    protected void afterSubMarkupText(DiffNode sourceNode, DiffNode targetNode) {
        if (decorator != null) {
            decorator.afterSubMarkupText(sourceNode, sourceResult, targetNode, targetResult);
        }
    }

    protected abstract void subCompare(CharSequence source, CharSequence target, DiffMarker<T> subMarker);
}