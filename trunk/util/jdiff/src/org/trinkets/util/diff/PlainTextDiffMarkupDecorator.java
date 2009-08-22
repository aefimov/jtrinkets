package org.trinkets.util.diff;

/**
 * Plain text decorator for tests.
 *
 * @author Alexey Efimov
 */
public class PlainTextDiffMarkupDecorator implements StringBuilderDiffMarkupDecorator {
    private static void appendMarkers(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        if (targetType.equals(DiffNode.Type.ADDED)) {
            targetResult.append("+");
        }
        if (sourceType.equals(DiffNode.Type.REMOVED)) {
            sourceResult.append("-");
        }
    }

    private static void appendSubMarkers(StringBuilder sourceResult, StringBuilder targetResult) {
        targetResult.append("*");
        sourceResult.append("*");
    }

    public void beforeMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        appendMarkers(sourceType, sourceResult, targetType, targetResult);
    }

    public void afterMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        appendMarkers(sourceType, sourceResult, targetType, targetResult);
    }

    public void beforeSubMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        appendSubMarkers(sourceResult, targetResult);
    }

    public void afterSubMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        appendSubMarkers(sourceResult, targetResult);
    }
}
