package org.trinkets.util.diff;

/**
 * Simple html markup decorator.
 * Decorates nodes with <code>&lt;b class="b-diff-&lt;lowercased diff node type>">..&lt;/b></code>.
 *
 * @author Alexey Efimov
 */
public abstract class HtmlMarkupDecorator implements StringBuilderDiffMarkupDecorator {
    public void beforeMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        appendB(sourceResult, sourceNode.getType());
        appendB(targetResult, targetNode.getType());
    }

    private void appendB(StringBuilder result, DiffNode.Type type) {
        result.append("<b class=\"b-diff-").append(type.name().toLowerCase()).append("\">");
    }

    public void afterMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        sourceResult.append("</b>");
        targetResult.append("</b>");
    }

    public void beforeSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        beforeMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }

    public void afterSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        afterMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }
}
