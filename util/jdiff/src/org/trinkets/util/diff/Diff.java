package org.trinkets.util.diff;

/**
 * Diff listener.
 *
 * @author Alexey Efimov
 */
public final class Diff {
    private Diff() {
    }

    public static <T> void compare(T[] source, T[] target, DiffMarker<T> marker) {
        DiffNode nodes = DiffAlgorithm.compare(source, target);

        // Markup diff results
        markup(source, target, marker, nodes.getFirst());
    }

    private static <T> void markup(T[] source, T[] target, DiffMarker<T> marker, DiffNode node) {
        int sourceOffset = 0;
        int targetOffset = 0;
        while (node != null) {
            DiffType sourceType = node.getType();
            DiffType targetType = node.hasOpposite() ? node.getOpposite().getType() : sourceType;

            int sourceLength = node.getLength();
            int targetLength = node.hasOpposite() ? node.getOpposite().getLength() : sourceLength;

            marker.apply(
                sourceType, source, sourceOffset, sourceLength,
                targetType, target, targetOffset, targetLength
            );

            sourceOffset += sourceLength;
            targetOffset += targetLength;

            node = node.getNext();
        }
    }

    public static void compareLines(String source, String target, DiffMarker<String> marker) {
        String[] sourceLines = Strings.lines(source);
        String[] targetLines = Strings.lines(target);

        DiffNode diff = DiffAlgorithm.compare(sourceLines, targetLines);
        diff = DiffAlgorithm.splitByLength(diff.getFirst(), 1);

        // Markup diff results
        markup(sourceLines, targetLines, marker, diff.getFirst());
    }

    public static void compareWords(String source, String target, DiffMarker<String> marker) {
        compare(Strings.words(source), Strings.words(target), marker);
    }

    public static void compareChars(String source, String target, DiffMarker<Character> marker) {
        compare(Strings.toArray(source), Strings.toArray(target), marker);
    }
}
