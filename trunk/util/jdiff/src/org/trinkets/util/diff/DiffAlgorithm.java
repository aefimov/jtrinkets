package org.trinkets.util.diff;

import java.util.ArrayList;
import java.util.List;

/**
 * Diff algorithm implementation.
 * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem.
 * <p/>
 * The result of this class is sequence of {@link org.trinkets.util.diff.DiffInstruction}s.
 *
 * @author Alexey Efimov
 */
final class DiffAlgorithm {
    private DiffAlgorithm() {
    }

    /**
     * Read diff from LCS matrix to list of {@link DiffInstruction}s.
     * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem#Print_the_diff for more
     * details.
     *
     * @param x    X list
     * @param y    Y list
     * @param c    LCS matrix
     * @param i    LCS matrix index i
     * @param j    LCS matrix index j
     * @param diff Result storage for {@link DiffInstruction}s.
     */
    private static <T> void backtrack(ArrayRange<T> x, ArrayRange<T> y, int[][] c, int i, int j, List<DiffInstruction> diff) {
        if (i > 0 && j > 0 && equals(x.get(i - 1), y.get(j - 1))) {
            backtrack(x, y, c, i - 1, j - 1, diff);
            addInstruction(diff, DiffInstruction.Type.UNCHANGED, 1);
        } else {
            if (j > 0 && (i == 0 || c[i][j - 1] >= c[i - 1][j])) {
                backtrack(x, y, c, i, j - 1, diff);
                addInstruction(diff, DiffInstruction.Type.ADDED, 1);
            } else if (i > 0 && (j == 0 || c[i][j - 1] < c[i - 1][j])) {
                backtrack(x, y, c, i - 1, j, diff);
                addInstruction(diff, DiffInstruction.Type.REMOVED, 1);
            }
        }
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
     * Add single {@link DiffInstruction} into list. If
     * previous {@link DiffInstruction} was appended with the same
     * {@link DiffInstruction.Type} as this one, then token was appended into previous list.
     *
     * @param diff   Result list of tokens
     * @param type   Diff type
     * @param length Marker length
     */
    private static void addInstruction(List<DiffInstruction> diff, DiffInstruction.Type type, int length) {
        // Check previous type
        if (diff.size() > 0 && type.equals(diff.get(diff.size() - 1).getType())) {
            // Append to previous with same type
            diff.get(diff.size() - 1).add(1);
        } else {
            diff.add(new DiffInstruction(type, length));
        }
    }

    /**
     * Compare arrays of values and return diff.
     *
     * @param x X list
     * @param y Y list
     * @return list of {@link DiffInstruction}
     */
    public static <T> List<DiffInstruction> compare(T[] x, T[] y) {
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
        List<DiffInstruction> diff = new ArrayList<DiffInstruction>();
        if (startX > 0) {
            // Begin not changed
            addInstruction(diff, DiffInstruction.Type.UNCHANGED, startX);
        }

        // Compute matrix of LCS (see http://en.wikipedia.org/wiki/Longest_common_subsequence_problem)
        int[][] c = lcs(xRange, yRange);
        backtrack(xRange, yRange, c, xRange.length(), yRange.length(), diff);

        if (endX < x.length) {
            // Ending not changed
            addInstruction(diff, DiffInstruction.Type.UNCHANGED, x.length - endX);
        }
        return diff;
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
