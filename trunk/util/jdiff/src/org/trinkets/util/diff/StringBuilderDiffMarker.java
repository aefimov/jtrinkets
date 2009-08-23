package org.trinkets.util.diff;

/**
 * Simple implementation of {@link org.trinkets.util.diff.DiffMarker}.
 *
 * @author Alexey Efimov
 */
public abstract class StringBuilderDiffMarker<T> implements DiffMarker<T> {
    protected final StringBuilder sourceResult = new StringBuilder();
    protected final StringBuilder targetResult = new StringBuilder();
    protected final StringBuilderDiffMarkupDecorator decorator;

    protected StringBuilderDiffMarker(StringBuilderDiffMarkupDecorator decorator) {
        this.decorator = decorator;
    }

    public String getSourceResult() {
        return sourceResult.toString();
    }

    public String getTargetResult() {
        return targetResult.toString();
    }

    public final void apply(DiffNode sourceNode, T[] source, int sourceOffset, int sourceLength,
                            DiffNode targetNode, T[] target, int targetOffset, int targetLength) {
        apply(
            sourceNode, escape(toCharSequence(source, sourceOffset, sourceLength)),
            targetNode, escape(toCharSequence(target, targetOffset, targetLength)));
    }

    protected CharSequence escape(CharSequence chars) {
        if (decorator != null) {
            return decorator.escape(chars);
        }
        return chars;
    }

    public void reset() {
        sourceResult.setLength(0);
        targetResult.setLength(0);
    }

    protected void apply(DiffNode sourceNode, CharSequence source, DiffNode targetNode, CharSequence target) {
        beforeMarkupText(sourceNode, targetNode);
        sourceResult.append(source);
        targetResult.append(target);
        afterMarkupText(sourceNode, targetNode);
    }

    protected abstract CharSequence toCharSequence(T[] array, int offset, int length);

    protected void beforeMarkupText(DiffNode sourceNode, DiffNode targetNode) {
        if (decorator != null) {
            decorator.beforeMarkupText(sourceNode, sourceResult, targetNode, targetResult);
        }
    }

    protected void afterMarkupText(DiffNode sourceNode, DiffNode targetNode) {
        if (decorator != null) {
            decorator.afterMarkupText(sourceNode, sourceResult, targetNode, targetResult);
        }
    }
}
