package org.trinkets.util.diff;

/**
 * Plain text markup. Used for tests.
 *
 * @author Alexey Efimov
 */
public abstract class PlainTextDiffMarker<T> extends StringBuilderDiffMarker<T> {
    protected void appendAddedRemovedMarks(DiffNode.Type sourceType, DiffNode.Type targetType) {
        if (DiffNode.Type.REMOVED.equals(sourceType)) {
            sourceResult.append("-");
        }
        if (DiffNode.Type.ADDED.equals(targetType)) {
            targetResult.append("+");
        }
    }

    @Override
    protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
        appendAddedRemovedMarks(sourceType, targetType);
    }

    @Override
    protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
        appendAddedRemovedMarks(sourceType, targetType);
    }
}
