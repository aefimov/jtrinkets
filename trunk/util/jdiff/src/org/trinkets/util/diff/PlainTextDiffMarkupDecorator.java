package org.trinkets.util.diff;

/**
 * Plain text decorator for tests.
 *
 * @author Alexey Efimov
 */
public class PlainTextDiffMarkupDecorator implements StringBuilderDiffMarkupDecorator {
    public void beforeMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        if (DiffNode.Type.ADDED.equals(targetNode.getType())) {
            targetResult.append("+");
        }
        if (DiffNode.Type.REMOVED.equals(sourceNode.getType())) {
            sourceResult.append("-");
        }
    }

    public void afterMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        beforeMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }

    public void beforeSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        targetResult.append("*");
        sourceResult.append("*");
    }

    public void afterSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        beforeSubMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }

    public CharSequence escape(CharSequence chars) {
        return chars;
    }
}
