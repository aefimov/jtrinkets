package org.trinkets.util.diff;

/**
 * Diff algorithm implementation.
 * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem.
 * <p/>
 * The result of this class is sequence of {@link DiffNode}s.
 *
 * @author Alexey Efimov
 */
final class DiffAlgorithm {
    private DiffAlgorithm() {
    }

    /**
     * Read diff from LCS matrix to list of {@link DiffNode}s.
     * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem#Print_the_diff for more
     * details.
     *
     * @param x    X list
     * @param y    Y list
     * @param c    LCS matrix
     * @param i    LCS matrix index i
     * @param j    LCS matrix index j
     * @param diff Start {@link org.trinkets.util.diff.DiffNode}.
     * @return End {@link org.trinkets.util.diff.DiffNode}.
     */
    private static <T> DiffNode backtrack(ArrayRange<T> x, ArrayRange<T> y, int[][] c, int i, int j, DiffNode diff) {
        if (i > 0 && j > 0 && equals(x.get(i - 1), y.get(j - 1))) {
            diff = backtrack(x, y, c, i - 1, j - 1, diff);
            return nextNode(diff, DiffType.UNCHANGED, 1);
        } else {
            if (j > 0 && (i == 0 || c[i][j - 1] >= c[i - 1][j])) {
                diff = backtrack(x, y, c, i, j - 1, diff);
                return nextNode(diff, DiffType.ADDED, 1);
            } else if (i > 0 && (j == 0 || c[i][j - 1] < c[i - 1][j])) {
                diff = backtrack(x, y, c, i - 1, j, diff);
                return nextNode(diff, DiffType.REMOVED, 1);
            }
        }
        return diff;
    }

    /**
     * Calculate LCS matrix.
     * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem
     * for more information.
     *
     * @param x X list
     * @param y Y list
     * @return LCS matrix
     */
    static <T> int[][] lcs(ArrayRange<T> x, ArrayRange<T> y) {
        // Create matrix
        int[][] c = new int[x.length() + 1][y.length() + 1];
        // Fill matrix 
        for (int i = 1; i < c.length; i++) {
            for (int j = 1; j < c[i].length; j++) {
                if (equals(x.get(i - 1), y.get(j - 1))) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                } else {
                    c[i][j] = Math.max(c[i][j - 1], c[i - 1][j]);
                }
            }
        }
        return c;
    }

    private static <T> boolean equals(T t1, T t2) {
        return t1 == t2 || t1 != null && t1.equals(t2);
    }

    /**
     * Add single {@link DiffNode} into list. If
     * previous {@link DiffNode} was appended with the same
     * {@link DiffType} as this one, then token was appended into previous list.
     *
     * @param previous Previous node
     * @param type     Diff type
     * @param length   Marker length
     * @return Next node
     */
    private static DiffNode nextNode(DiffNode previous, DiffType type, int length) {
        // Check previous type
        if (previous != null && type.equals(previous.getType())) {
            // Append to previous with same type
            previous.setLength(previous.getLength() + 1);
            return previous;
        } else {
            DiffNode next = new DiffNode(type, length);
            next.setPrevious(previous);
            if (previous != null) {
                previous.setNext(next);
            }
            return next;
        }
    }

    /**
     * Compare arrays of values and return diff.
     *
     * @param x X list
     * @param y Y list
     * @return list of {@link DiffNode}
     */
    public static <T> DiffNode compare(T[] x, T[] y) {
        int startX = 0;
        int startY = 0;
        // Skip equal objects at start
        while (startX < x.length && startY < y.length && equals(y[startY], x[startX])) {
            startX++;
            startY++;
        }
        int endX = x.length;
        int endY = y.length;
        // Skip equal objects at end
        while (endX > startX && endY > startY && equals(x[endX - 1], y[endY - 1])) {
            endX--;
            endY--;
        }
        // Now we have sequence 0..startX..endX..x.length() and
        //                      0..startY..endY..y.length()
        ArrayRange<T> xRange = new ArrayRange<T>(x, startX, endX);
        ArrayRange<T> yRange = new ArrayRange<T>(y, startY, endY);
        // To build C matrix we must use only x[startX..endX] comparing to y[startY..endY]
        DiffNode diff = null;
        if (startX > 0) {
            // Begin not changed
            diff = nextNode(diff, DiffType.UNCHANGED, startX);
        }

        // Compute matrix of LCS (see http://en.wikipedia.org/wiki/Longest_common_subsequence_problem)
        int[][] c = lcs(xRange, yRange);
        diff = backtrack(xRange, yRange, c, xRange.length(), yRange.length(), diff);

        if (endX < x.length) {
            // Ending not changed
            diff = nextNode(diff, DiffType.UNCHANGED, x.length - endX);
        }
        return diff.getFirst();
    }

    static final class ArrayRange<T> {
        private final T[] array;
        private final int base;
        private final int length;

        public ArrayRange(T[] array, int start, int end) {
            this.array = array;
            this.base = start;
            this.length = end - start;
        }

        public T get(int i) {
            return array[base + i];
        }

        public int length() {
            return length;
        }
    }
}
