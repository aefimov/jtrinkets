package org.trinkets.util.diff;

/**
 * Html table markup decorator.
 * Decorates nodes with <code>&lt;th>${line number}&lt;/th>&lt;tb class="b-diff-${lowercased diff node type} (middle|first|last)">..&lt;/td>\n</code>.
 * Where 'middle' is line with previous and next nodes has same type as this. The 'first' and 'last' has only one the same node.
 *
 * @author Alexey Efimov
 */
public abstract class HtmlTableMarkupDecorator implements StringBuilderDiffMarkupDecorator {
    private int sourceLine = 1;
    private int targetLine = 1;

    public void beforeMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        sourceLine = appendLineNumber(sourceResult, sourceNode.getType(), sourceLine);
        appendTd(sourceResult, sourceNode);

        targetLine = appendLineNumber(targetResult, targetNode.getType(), targetLine);
        appendTd(targetResult, targetNode);
    }

    private int appendLineNumber(StringBuilder builder, DiffNode.Type type, int line) {
        builder.append("<th>").append(!DiffNode.Type.VIRTUAL.equals(type) ? line++ : "").append("</th>");
        return line;
    }

    private void appendTd(StringBuilder builder, DiffNode node) {
        builder.append("<td class=\"diff-");
        DiffNode.Type type = node.getType();
        builder.append(type.name().toLowerCase());
        boolean samePrevious = hasSamePreviousType(node);
        boolean sameNext = hasSameNextType(node);
        if (samePrevious && sameNext) {
            builder.append(" middle");
        } else if (samePrevious) {
            builder.append(" last");
        } else if (sameNext) {
            builder.append(" first");
        }
        builder.append("\">");
    }

    private boolean hasSamePreviousType(DiffNode node) {
        DiffNode.Type type = node.getType();
        if (node.hasPrevious()) {
            if (type.equals(node.getPrevious().getType()) ||
                DiffNode.Type.VIRTUAL.equals(node.getPrevious().getType()) ||
                DiffNode.Type.VIRTUAL.equals(type) && node.getPrevious().getType().isChanged()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSameNextType(DiffNode node) {
        DiffNode.Type type = node.getType();
        if (node.hasNext()) {
            if (type.equals(node.getNext().getType()) ||
                DiffNode.Type.VIRTUAL.equals(node.getNext().getType()) ||
                DiffNode.Type.VIRTUAL.equals(type) && node.getNext().getType().isChanged()) {
                return true;
            }
        }
        return false;
    }

    public void afterMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        sourceResult.append("</td>\n");
        targetResult.append("</td>\n");
    }

    public void beforeSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        beforeMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }

    public void afterSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult) {
        afterMarkupText(sourceNode, sourceResult, targetNode, targetResult);
    }
}
