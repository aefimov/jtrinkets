package org.trinkets.util.diff;

/**
 * Markup decorator.
 *
 * @author Alexey Efimov
 */
public interface StringBuilderDiffMarkupDecorator {
    void beforeMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult);


    void afterMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult);


    void beforeSubMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult);


    void afterSubMarkupText(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult);
}
