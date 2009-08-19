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

        // Join changed nodes
        joinChanged(nodes.getFirst());

        // Add virtual nodes
        addVirtual(nodes.getFirst());

        // Markup diff results
        markup(source, target, marker, nodes.getFirst());
    }

    private static void joinChanged(DiffNode node) {
        while (node != null) {
            DiffNode next = node.getNext();
            if (next != null &&
                !node.hasOpposite() &&
                !next.hasOpposite() &&
                !DiffType.UNCHANGED.equals(node.getType()) &&
                !DiffType.UNCHANGED.equals(next.getType())) {
                next.setOpposite(node);
                node.setOpposite(next);
                next.setPrevious(node.getPrevious());
                node.setNext(next.getNext());
                if (DiffType.ADDED.equals(node.getType())) {
                    if (node.hasPrevious()) {
                        node.getPrevious().setNext(next);
                    }
                    if (node.hasNext()) {
                        node.getNext().setPrevious(next);
                    }
                }
            }
            node = node.getNext();
        }
    }

    private static void addVirtual(DiffNode node) {
        while (node != null) {
            if (!node.hasOpposite() &&
                !DiffType.UNCHANGED.equals(node.getType())) {
                DiffNode virtual = new DiffNode(DiffType.VIRTUAL, 0);
                virtual.setOpposite(node);
                node.setOpposite(virtual);
                virtual.setNext(node.getNext());
                virtual.setPrevious(node.getPrevious());
                if (DiffType.ADDED.equals(node.getType())) {
                    if (node.hasPrevious()) {
                        node.getPrevious().setNext(virtual);
                    }
                    if (node.hasNext()) {
                        node.getNext().setPrevious(virtual);
                    }
                }
            }
            node = node.getNext();
        }
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
        compare(Strings.lines(source), Strings.lines(target), marker);
    }

    public static void compareWords(String source, String target, DiffMarker<String> marker) {
        compare(Strings.words(source), Strings.words(target), marker);
    }

    public static void compareChars(String source, String target, DiffMarker<Character> marker) {
        compare(Strings.toArray(source), Strings.toArray(target), marker);
    }
}
