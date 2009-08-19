package org.trinkets.util.diff;

/**
 * Diff marker.
 *
 * @author Alexey Efimov
 */
public interface DiffMarker<T> {
    void apply(DiffType sourceType, T[] source, int sourceOffset, int sourceLength,
               DiffType targetType, T[] target, int targetOffset, int targetLength);
}
