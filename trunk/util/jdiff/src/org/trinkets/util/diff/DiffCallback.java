package org.trinkets.util.diff;

/**
 * Diff listener.
 *
 * @author Alexey Efimov
 */
public interface DiffCallback<T> {
    void unchanged(T[] source, int sourceOffset, T[] target, int targetOffset, int length);

    void added(T[] source, int sourceOffset, T[] target, int targetOffset, int length);

    void removed(T[] source, int sourceOffset, T[] target, int targetOffset, int length);
}
