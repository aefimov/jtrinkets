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
    protected final void markup(DiffNode.Type sourceType, T[] source, int sourceOffset, int sourceLength,
                          DiffNode.Type targetType, T[] target, int targetOffset, int targetLength) {
        markupText(
            sourceType, toCharSequence(source, sourceOffset, sourceLength),
            targetType, toCharSequence(target, targetOffset, targetLength));
    }

    protected void markupText(DiffNode.Type sourceType, CharSequence source, DiffNode.Type targetType, CharSequence target) {
        beforeMarkupText(sourceType, targetType);
        sourceResult.append(source);
        targetResult.append(target);
        afterMarkupText(sourceType, targetType);
    }

    protected abstract CharSequence toCharSequence(T[] array, int offset, int length);

    protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
    }

    protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
    }
}
