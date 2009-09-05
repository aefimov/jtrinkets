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
     * @return Backtrack in {@link org.trinkets.util.diff.DiffNode}.
     */
    private static <T> DiffNode backtrack(T[] x, T[] y) {
        DiffNode backtrack = null;
        int[][] lcs = lcs(x, y);
        // From back to begin
        int i = x.length, j = y.length;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && equals(x[i - 1], y[j - 1])) {
                backtrack = createNode(backtrack, DiffNode.Type.UNCHANGED, 1);
                i--;
                j--;
            } else if (j > 0 && (i == 0 || lcs[i][j - 1] >= lcs[i - 1][j])) {
                backtrack = createNode(backtrack, DiffNode.Type.ADDED, 1);
                j--;
            } else if (i > 0 && (j == 0 || lcs[i][j - 1] < lcs[i - 1][j])) {
                backtrack = createNode(backtrack, DiffNode.Type.REMOVED, 1);
                i--;
            }
        }
        return backtrack != null ? backtrack.reverse() : null;
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
    static <T> int[][] lcs(T[] x, T[] y) {
        // Create matrix
        int[][] lcs = new int[x.length + 1][y.length + 1];
        // Fill matrix
        for (int i = 1; i < lcs.length; i++) {
            for (int j = 1; j < lcs[i].length; j++) {
                if (equals(x[i - 1], y[j - 1])) {
                    lcs[i][j] = lcs[i - 1][j - 1] + 1;
                } else {
                    lcs[i][j] = Math.max(lcs[i][j - 1], lcs[i - 1][j]);
                }
            }
        }
        return lcs;
    }

    private static <T> boolean equals(T t1, T t2) {
        return t1 == t2 || t1 != null && t1.equals(t2);
    }

    /**
     * Add single {@link DiffNode} into list. If
     * previous {@link DiffNode} was appended with the same
     * {@link org.trinkets.util.diff.DiffNode.Type} as this one, then token was appended into previous list.
     *
     * @param current Current node
     * @param type    Diff type
     * @param length  Marker length
     * @return Next node
     */
    private static DiffNode createNode(DiffNode current, DiffNode.Type type, int length) {
        // Check previous type
        if (current != null && type.equals(current.getType())) {
            // Append to previous with same type
            current.setLength(current.getLength() + 1);
            return current;
        } else {
            DiffNode created = new DiffNode(type, length);
            created.setPrevious(current);
            return created;
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
        // Here was optimisation to skip equals symbols at begin and at end of arrays,
        // but it was removed because it doing wrong comparison to indenting code
        // for example

        // Build LCS matrix and backtrack it
        DiffNode diff = backtrack(x, y);

        // Split nodes
        diff = split(diff.getFirst());

        return diff.getFirst();
    }

    private static DiffNode split(DiffNode node) {
        DiffNode previous = node != null ? node.getFirst() : null;
        while (node != null) {
            if (DiffNode.Type.UNCHANGED.equals(node.getType())) {
                // Simple split by clone
                DiffNode opposite = new DiffNode(DiffNode.Type.UNCHANGED, node.getLength());
                node.setOpposite(opposite);
            } else if (DiffNode.Type.ADDED.equals(node.getType())) {
                DiffNode next = node.getNext();
                if (next != null && DiffNode.Type.REMOVED.equals(next.getType())) {
                    previous = node.remove();
                    next.setOpposite(node);
                    node = previous;
                } else {
                    DiffNode virtual = new DiffNode(DiffNode.Type.VIRTUAL, 0);
                    node.insertBefore(virtual);
                    previous = node.remove();
                    virtual.setOpposite(node);
                    node = previous;
                }
            } else if (DiffNode.Type.REMOVED.equals(node.getType())) {
                DiffNode next = node.getNext();
                if (next != null && DiffNode.Type.ADDED.equals(next.getType())) {
                    previous = next.remove();
                    node.setOpposite(next);
                } else {
                    DiffNode virtual = new DiffNode(DiffNode.Type.VIRTUAL, 0);
                    node.setOpposite(virtual);
                }
            }
            node = node.getNext();
        }
        return previous != null ? previous.getFirst() : null;
    }

    public static <T> DiffNode splitChanged(DiffNode node, T[] x, T[] y, IncrementalDiffHandler<T> incrementalDiffHandler) {
        DiffNode previous = node != null ? node.getFirst() : null;
        while (node != null) {
            // If added items not equals to count of removed items
            if (incrementalDiffHandler != null &&
                node.hasOpposite() &&
                DiffNode.Type.REMOVED.equals(node.getType()) &&
                DiffNode.Type.ADDED.equals(node.getOpposite().getType()) &&
                node.getLength() != node.getOpposite().getLength()) {
                int minLength = Math.min(node.getLength(), node.getOpposite().getLength());
                if (minLength > 0) {
                    // Try to find best diff with minimal changes
                    int maxUnchanged = 0;
                    int index = 0;
                    int delta = Math.abs(node.getLength() - node.getOpposite().getLength());
                    for (int i = 0; i <= delta; i++) {
                        int xOffset = node.getLength() > minLength ? i : 0;
                        int yOffset = node.getOpposite().getLength() > minLength ? i : 0;
                        // Compare ranges
                        DiffNode diff = incrementalDiffHandler.diff(
                            x, node.getOffset() + xOffset, minLength,
                            y, node.getOpposite().getOffset() + yOffset, minLength);

                        int unchanged = getUnchangedLength(diff.getFirst());
                        if (unchanged > maxUnchanged) {
                            maxUnchanged = unchanged;
                            index = i;
                        }
                    }
                    if (maxUnchanged > 0) {
                        // We found diff with minimal changes
                        if (node.getLength() < node.getOpposite().getLength()) {
                            // Removed items is less that added items
                            // Insert virtual before
                            if (index > 0) {
                                DiffNode newNode = new DiffNode(DiffNode.Type.VIRTUAL, 0);
                                newNode.setOpposite(new DiffNode(node.getOpposite().getType(), index));
                                node.insertBefore(newNode);
                                node.getOpposite().insertBefore(newNode.getOpposite());
                                node.getOpposite().setLength(node.getOpposite().getLength() - index);
                            }
                            // Insert virtual after
                            if (minLength < node.getOpposite().getLength()) {
                                int remain = node.getOpposite().getLength() - minLength;
                                DiffNode newNode = new DiffNode(DiffNode.Type.VIRTUAL, 0);
                                newNode.setOpposite(new DiffNode(node.getOpposite().getType(), remain));
                                node.insertAfter(newNode);
                                node.getOpposite().insertAfter(newNode.getOpposite());
                                node.getOpposite().setLength(node.getOpposite().getLength() - remain);
                            }
                        } else {
                            // Insert virtual before
                            if (index > 0) {
                                DiffNode newNode = new DiffNode(node.getType(), index);
                                newNode.setOpposite(new DiffNode(DiffNode.Type.VIRTUAL, 0));
                                node.insertBefore(newNode);
                                node.getOpposite().insertBefore(newNode.getOpposite());
                                node.setLength(node.getLength() - index);
                            }
                            // Insert virtual after
                            if (minLength < node.getLength()) {
                                int remain = node.getLength() - minLength;
                                DiffNode newNode = new DiffNode(node.getType(), remain);
                                newNode.setOpposite(new DiffNode(DiffNode.Type.VIRTUAL, 0));
                                node.insertAfter(newNode);
                                node.getOpposite().insertAfter(newNode.getOpposite());
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
            if (DiffNode.Type.UNCHANGED.equals(node.getType())) {
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
}
