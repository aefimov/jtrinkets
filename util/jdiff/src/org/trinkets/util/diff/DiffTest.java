package org.trinkets.util.diff;

import junit.framework.TestCase;

/**
 * Diff tests.
 *
 * @author Alexey Efimov
 */
public class DiffTest extends TestCase {
    public void testLcs() {
        Character[] x = Strings.toArray("XMJYAUZ".toCharArray());
        Character[] y = Strings.toArray("MZJAWXU".toCharArray());
        int[][] c = DiffAlgorithm.lcs(
            new DiffAlgorithm.ArrayRange<Character>(x, 0, x.length),
            new DiffAlgorithm.ArrayRange<Character>(y, 0, y.length)
        );
        StringBuilder builder = new StringBuilder();
        for (int[] cx : c) {
            for (int cy : cx) {
                builder.append(cy);
                builder.append(' ');
            }
        }
        assertEquals("0 0 0 0 0 0 0 0 " +
            "0 0 0 0 0 0 1 1 " +
            "0 1 1 1 1 1 1 1 " +
            "0 1 1 2 2 2 2 2 " +
            "0 1 1 2 2 2 2 2 " +
            "0 1 1 2 3 3 3 3 " +
            "0 1 1 2 3 3 3 4 " +
            "0 1 2 2 3 3 3 4 ", builder.toString());
    }

    public void testCompareCharacters() {
        CharsDiffCallback diffCallback = new CharsDiffCallback();
        Diff.compareChars("XMJYAUZ", "MZJAWXU", diffCallback);

        assertEquals("-X-MJ-Y-AU-Z-", diffCallback.getSourceResult());
        assertEquals("M+Z+JA+WX+U", diffCallback.getTargetResult());
    }

    public void testCompareWords() {
        StringDiffCallback diffCallback = new StringDiffCallback();
        Diff.compareWords(
            "The red brown fox jumped over the roling log",
            "The brown spotted fox leaped over the rolling log",
            diffCallback
        );

        assertEquals("The -red -brown fox -jumped- over the -roling- log", diffCallback.getSourceResult());
        assertEquals("The brown+ spotted+ fox +leaped+ over the +rolling+ log", diffCallback.getTargetResult());
    }

    public void testCompareLines() {
        StringDiffCallback diffCallback = new StringDiffCallback();
        Diff.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", diffCallback);

        assertEquals("-The red brown fox\n" +
            "jumped over the roling log-", diffCallback.getSourceResult());
        assertEquals("+The brown spotted fox\n" +
            "leaped over the rolling log+", diffCallback.getTargetResult());
    }

    private static class CharsDiffCallback implements DiffCallback<Character> {
        private final StringBuilder sourceResult = new StringBuilder();
        private final StringBuilder targetResult = new StringBuilder();

        public void unchanged(Character[] source, int sourceOffset, Character[] target, int targetOffset, int length) {
            sourceResult.append(Strings.toArray(source, sourceOffset, length));
            targetResult.append(Strings.toArray(target, targetOffset, length));
        }

        public void added(Character[] source, int sourceOffset, Character[] target, int targetOffset, int length) {
            targetResult.append("+");
            targetResult.append(Strings.toArray(target, targetOffset, length));
            targetResult.append("+");
        }

        public void removed(Character[] source, int sourceOffset, Character[] target, int targetOffset, int length) {
            sourceResult.append("-");
            sourceResult.append(Strings.toArray(source, sourceOffset, length));
            sourceResult.append("-");
        }

        public String getSourceResult() {
            return sourceResult.toString();
        }

        public String getTargetResult() {
            return targetResult.toString();
        }
    }

    private static class StringDiffCallback implements DiffCallback<String> {
        private final StringBuilder sourceResult = new StringBuilder();
        private final StringBuilder targetResult = new StringBuilder();

        public void unchanged(String[] source, int sourceOffset, String[] target, int targetOffset, int length) {
            sourceResult.append(Strings.toCharSequence(source, sourceOffset, length));
            targetResult.append(Strings.toCharSequence(target, targetOffset, length));
        }

        public void added(String[] source, int sourceOffset, String[] target, int targetOffset, int length) {
            targetResult.append("+");
            targetResult.append(Strings.toCharSequence(target, targetOffset, length));
            targetResult.append("+");
        }

        public void removed(String[] source, int sourceOffset, String[] target, int targetOffset, int length) {
            sourceResult.append("-");
            sourceResult.append(Strings.toCharSequence(source, sourceOffset, length));
            sourceResult.append("-");
        }

        public String getSourceResult() {
            return sourceResult.toString();
        }

        public String getTargetResult() {
            return targetResult.toString();
        }
    }
}
