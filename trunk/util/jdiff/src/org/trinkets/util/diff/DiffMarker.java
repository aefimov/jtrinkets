package org.trinkets.util.diff;

/**
 * Diff marker. Marker is used to markup diff results.
 * This is a converter from {@link org.trinkets.util.diff.DiffNode} structure to plain text or HTML.
 *
 * @author Alexey Efimov
 * @see org.trinkets.util.diff.StringBuilderDiffMarker
 * @see org.trinkets.util.diff.DiffMarkup
 */
public interface DiffMarker<T> {
    void apply(DiffNode sourceNode, T[] source, int sourceOffset, int sourceLength,
               DiffNode targetNode, T[] target, int targetOffset, int targetLength);
    void reset();
}
