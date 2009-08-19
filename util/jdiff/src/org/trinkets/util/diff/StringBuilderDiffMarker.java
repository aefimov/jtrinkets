package org.trinkets.util.diff;

/**
 * Simple implementation of {@link org.trinkets.util.diff.DiffMarker}.
 *
 * @author Alexey Efimov
 */
public abstract class StringBuilderDiffMarker<T> extends StatisticsDiffMarker<T> {
    protected final StringBuilder sourceResult = new StringBuilder();
    protected final StringBuilder targetResult = new StringBuilder();

    public String getSourceResult() {
        return sourceResult.toString();
    }

    public String getTargetResult() {
        return targetResult.toString();
    }

    @Override
    protected final void markup(DiffType sourceType, T[] source, int sourceOffset, int sourceLength,
                          DiffType targetType, T[] target, int targetOffset, int targetLength) {
        markupText(
            sourceType, toCharSequence(source, sourceOffset, sourceLength),
            targetType, toCharSequence(target, targetOffset, targetLength));
    }

    protected void markupText(DiffType sourceType, CharSequence source, DiffType targetType, CharSequence target) {
        beforeMarkupText(sourceType, targetType);
        sourceResult.append(source);
        targetResult.append(target);
        afterMarkupText(sourceType, targetType);
    }

    protected abstract CharSequence toCharSequence(T[] array, int offset, int length);

    protected void beforeMarkupText(DiffType sourceType, DiffType targetType) {
    }

    protected void afterMarkupText(DiffType sourceType, DiffType targetType) {
    }
}
