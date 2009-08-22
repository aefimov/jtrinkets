package org.trinkets.util.diff;

/**
 * Diff algorithm implementation.
 * See http://en.wikipedia.org/wiki/Longest_common_subsequence_problem.
 * <p/>
 * The result of this class is sequence of {@link DiffNode}s.
 *
 * @author Alexey Efimov
 */
public final class DiffAlgorithm {
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
        ArrayRange<T> xRange = new ArrayRange<T>(x, startX, endX - startX);
        ArrayRange<T> yRange = new ArrayRange<T>(y, startY, endY - startY);
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

        // Split nodes
        diff = split(diff.getFirst());

        // Split opposite nodes with different length
        diff = splitChanged(diff.getFirst(), x, y);

        return diff.getFirst();
    }

    private static DiffNode split(DiffNode node) {
        DiffNode previous = node != null ? node.getFirst() : null;
        while (node != null) {
            if (DiffType.UNCHANGED.equals(node.getType())) {
                // Simple split by clone
                DiffNode opposite = new DiffNode(DiffType.UNCHANGED, node.getLength());
                node.setOpposite(opposite);
            } else if (DiffType.ADDED.equals(node.getType())) {
                DiffNode next = node.getNext();
                if (next != null && DiffType.REMOVED.equals(next.getType())) {
                    node.remove();
                    next.setOpposite(node);
                } else {
                    DiffNode virtual = new DiffNode(DiffType.VIRTUAL, 0);
                    node.insertBefore(virtual);
                    node.remove();
                    virtual.setOpposite(node);
                }
            } else if (DiffType.REMOVED.equals(node.getType())) {
                DiffNode next = node.getNext();
                if (next != null && DiffType.ADDED.equals(next.getType())) {
                    next.remove();
                    node.setOpposite(next);
                } else {
                    DiffNode virtual = new DiffNode(DiffType.VIRTUAL, 0);
                    node.setOpposite(virtual);
                }
            }
            node = node.getNext();
        }
        return previous != null ? previous.getFirst() : null;
    }

    private static <T> DiffNode splitChanged(DiffNode node, T[] x, T[] y) {
        DiffNode previous = node != null ? node.getFirst() : null;
        while (node != null) {
            // If added items not equals to count of removed items
            if (node.hasOpposite() &&
                DiffType.REMOVED.equals(node.getType()) &&
                DiffType.ADDED.equals(node.getOpposite().getType()) &&
                node.getLength() != node.getOpposite().getLength()) {
                int minLength = Math.min(node.getLength(), node.getOpposite().getLength());
                if (minLength > 0) {
                    // Try to find best diff with minimal changes
                    DiffNode maxUnchangedDiff = null;
                    int maxUnchanged = 0;
                    int index = 0;
                    int delta = Math.abs(node.getLength() - node.getOpposite().getLength());
                    for (int i = 0; i <= delta; i++) {
                        int xOffset = node.getLength() > minLength ? i : 0;
                        int yOffset = node.getOpposite().getLength() > minLength ? i : 0;
                        ArrayRange<T> xRange = new ArrayRange<T>(x, node.getOffset() + xOffset, minLength);
                        ArrayRange<T> yRange = new ArrayRange<T>(y, node.getOpposite().getOffset() + yOffset, minLength);

                        // Compare ranges
                        DiffNode diff = backtrack(
                            xRange, yRange,
                            lcs(xRange, yRange),
                            xRange.length(), yRange.length(),
                            null
                        );

                        int unchanged = getUnchangedLength(node.getFirst());
                        if (unchanged > maxUnchanged) {
                            maxUnchanged = unchanged;
                            maxUnchangedDiff = diff;
                            index = i;
                        }
                    }
                    if (maxUnchangedDiff != null) {
                        // We found diff with minimal changes
                        if (node.getLength() < node.getOpposite().getLength()) {
                            // Removed items is less that added items
                            // Insert virtual before
                            if (index > 0) {
                                DiffNode newNode = new DiffNode(DiffType.VIRTUAL, 0);
                                newNode.setOpposite(new DiffNode(node.getOpposite().getType(), index));
                                node.insertBefore(newNode);
                                node.getOpposite().setLength(node.getOpposite().getLength() - index);
                            }
                            // Insert virtual after
                            if (minLength < node.getOpposite().getLength()) {
                                int remain = node.getOpposite().getLength() - minLength;
                                DiffNode newNode = new DiffNode(DiffType.VIRTUAL, 0);
                                newNode.setOpposite(new DiffNode(node.getOpposite().getType(), remain));
                                node.insertAfter(newNode);
                                node.getOpposite().setLength(node.getOpposite().getLength() - remain);
                            }
                        } else {
                            // Insert virtual before
                            if (index > 0) {
                                DiffNode newNode = new DiffNode(node.getOpposite().getType(), index);
                                newNode.setOpposite(new DiffNode(DiffType.VIRTUAL, 0));
                                node.insertBefore(newNode);
                                node.getOpposite().insertBefore(newNode.getOpposite());
                                node.setLength(node.getLength() - index);
                            }
                            // Insert virtual after
                            if (minLength < node.getLength()) {
                                int remain = node.getLength() - minLength;
                                DiffNode newNode = new DiffNode(node.getType(), remain);
                                newNode.setOpposite(new DiffNode(DiffType.VIRTUAL, 0));
                                node.insertAfter(newNode);
                                node.setLength(node.getLength() - remain);
                            }
                        }
                    }
                }
            }
            node = node.getNext();
        }
        return previous != null ? previous.getFirst() : null;
    }

    private static int getUnchangedLength(DiffNode node) {
        int result = 0;
        while (node != null) {
            if (DiffType.UNCHANGED.equals(node.getType())) {
                result += node.getLength();
            }
            node = node.getNext();
        }
        return result;
    }

    public static DiffNode splitByLength(DiffNode node, int length) {
        DiffNode result = node;
        while (node != null) {
            node = node.splitByLength(length);
            result = node.getFirst();
            node = node.getNext();
        }
        return result;
    }

    static final class ArrayRange<T> {
        private final T[] array;
        private final int base;
        private final int length;

        public ArrayRange(T[] array, int start, int length) {
            this.array = array;
            this.base = start;
            this.length = length;
        }

        public T get(int i) {
            return array[base + i];
        }

        public int length() {
            return length;
        }
    }
}
