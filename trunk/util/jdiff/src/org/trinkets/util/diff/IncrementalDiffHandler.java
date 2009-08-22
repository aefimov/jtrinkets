package org.trinkets.util.diff;

/**
 * Incremental diff support.
 *
 * @author Alexey Efimov
 */
public interface IncrementalDiffHandler<T> {
    DiffNode diff(T[] x, int xOffset, int xLength, T[] y, int yOffset, int yLength);
}
