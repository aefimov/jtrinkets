package org.trinkets.util.diff;

import java.util.List;

/**
 * Diff listener.
 *
 * @author Alexey Efimov
 */
public final class Diff {
    private Diff() {
    }

    public static <T> void compare(T[] source, T[] target, DiffCallback<T> callback) {
        int sourceOffset = 0;
        int targetOffset = 0;
        List<DiffInstruction> instructions = DiffAlgorithm.compare(source, target);
        for (DiffInstruction instruction : instructions) {
            switch (instruction.getType()) {
                case UNCHANGED: {
                    callback.unchanged(source, sourceOffset, target, targetOffset, instruction.getLength());
                    sourceOffset += instruction.getLength();
                    targetOffset += instruction.getLength();
                    break;
                }
                case ADDED: {
                    callback.added(source, sourceOffset, target, targetOffset, instruction.getLength());
                    targetOffset += instruction.getLength();
                    break;
                }
                case REMOVED: {
                    callback.removed(source, sourceOffset, target, targetOffset, instruction.getLength());
                    sourceOffset += instruction.getLength();
                    break;
                }
            }
        }
    }

    public static void compareLines(String source, String target, DiffCallback<String> callback) {
        compare(Strings.lines(source), Strings.lines(target), callback);
    }

    public static void compareWords(String source, String target, DiffCallback<String> callback) {
        compare(Strings.words(source), Strings.words(target), callback);
    }

    public static void compareChars(String source, String target, DiffCallback<Character> callback) {
        compare(Strings.toArray(source), Strings.toArray(target), callback);
    }
}
