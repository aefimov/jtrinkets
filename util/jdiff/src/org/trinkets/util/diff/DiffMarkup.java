package org.trinkets.util.diff;

/**
 * Diff markup utility.
 *
 * @author Alexey Efimov
 */
public final class DiffMarkup {
    private DiffMarkup() {
    }

    public static <T> void compare(T[] source, T[] target, DiffMarker<T> marker) {
        DiffNode diff = DiffAlgorithm.compare(source, target);

        // Markup diff results
        markup(source, target, marker, diff.getFirst());
    }

    private static <T> void markup(T[] source, T[] target, DiffMarker<T> marker, DiffNode node) {
        int sourceOffset = 0;
        int targetOffset = 0;
        while (node != null) {
            DiffNode.Type sourceType = node.getType();
            DiffNode.Type targetType = node.hasOpposite() ? node.getOpposite().getType() : sourceType;

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
        // Split opposite nodes with different length
        diff = DiffAlgorithm.splitChanged(diff.getFirst(), sourceLines, targetLines, new IncrementalLinesDiffHandler());
        // Split by one line per change
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

    private static class IncrementalLinesDiffHandler implements IncrementalDiffHandler<String> {
        public DiffNode diff(String[] x, int xOffset, int xLength, String[] y, int yOffset, int yLength) {
            CharSequence xChars = Strings.toCharSequence(x, xOffset, xLength);
            CharSequence yChars = Strings.toCharSequence(y, yOffset, yLength);

            return DiffAlgorithm.compare(Strings.words(xChars.toString()), Strings.words(yChars.toString()));
        }
    }
}
