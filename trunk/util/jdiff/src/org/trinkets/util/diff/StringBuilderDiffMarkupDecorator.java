package org.trinkets.util.diff;

/**
 * Markup decorator.
 *
 * @author Alexey Efimov
 */
public interface StringBuilderDiffMarkupDecorator {
    void beforeMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult);


    void afterMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult);


    void beforeSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult);


    void afterSubMarkupText(DiffNode sourceNode, StringBuilder sourceResult, DiffNode targetNode, StringBuilder targetResult);

    CharSequence escape(CharSequence chars);
}
