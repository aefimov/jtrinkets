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
        CharsDiffMarker marker = new CharsDiffMarker();
        Diff.compareChars("XMJYAUZ", "MZJAWXU", marker);

        assertEquals("-X-MJ-Y-AU-Z-", marker.getSourceResult());
        assertEquals("M+Z+JA+WX+U", marker.getTargetResult());
    }

    public void testCompareWords() {
        StringDiffMarker marker = new StringDiffMarker();
        Diff.compareWords(
            "The red brown fox jumped over the roling log",
            "The brown spotted fox leaped over the rolling log",
            marker
        );

        assertEquals("The -red -brown fox -jumped- over the -roling- log", marker.getSourceResult());
        assertEquals("The brown+ spotted+ fox +leaped+ over the +rolling+ log", marker.getTargetResult());
    }

    public void testCompareLines() {
        StringDiffMarker marker = new StringDiffMarker();
        Diff.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("-The red brown fox\n" +
            "jumped over the roling log-", marker.getSourceResult());
        assertEquals("+The brown spotted fox\n" +
            "leaped over the rolling log+", marker.getTargetResult());
    }

    public void testCompareLinesIncremental() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("*The -red -brown fox\n-jumped- over the *roling* log*", marker.getSourceResult());
        assertEquals("*The brown+ spotted+ fox\n+leaped+ over the *rol+l+ing* log*", marker.getTargetResult());
    }

    private static abstract class PlainTextDiffMarket<T> extends StringBuilderDiffMarker<T> {
        protected void appendAddedRemovedMarks(DiffType sourceType, DiffType targetType) {
            if (DiffType.REMOVED.equals(sourceType)) {
                sourceResult.append("-");
            }
            if (DiffType.ADDED.equals(targetType)) {
                targetResult.append("+");
            }
        }

        @Override
        protected void beforeMarkupText(DiffType sourceType, DiffType targetType) {
            appendAddedRemovedMarks(sourceType, targetType);
        }

        @Override
        protected void afterMarkupText(DiffType sourceType, DiffType targetType) {
            appendAddedRemovedMarks(sourceType, targetType);
        }
    }

    private static class CharsDiffMarker extends PlainTextDiffMarket<Character> {
        @Override
        protected CharSequence toCharSequence(Character[] array, int offset, int length) {
            return new String(Strings.toArray(array, offset, length));
        }
    }

    private static class StringDiffMarker extends PlainTextDiffMarket<String> {
        @Override
        protected CharSequence toCharSequence(String[] array, int offset, int length) {
            return Strings.toCharSequence(array, offset, length);
        }
    }

    private static class IncrementalLinesDiffMarker extends StringDiffMarker {
        public void markupText(DiffType sourceType, CharSequence source, DiffType targetType, CharSequence target) {
            if (DiffType.REMOVED.equals(sourceType) && targetType.equals(DiffType.ADDED)) {
                // Compare words
                IncrementalWordsDiffMarker marker = new IncrementalWordsDiffMarker();
                Diff.compareWords(source.toString(), target.toString(), marker);

                if (marker.getAddedPercent() < 0.5 && marker.getRemovePercent() < 0.5) {
                    sourceResult.append("*");
                    sourceResult.append(marker.getSourceResult());
                    sourceResult.append("*");
                    targetResult.append("*");
                    targetResult.append(marker.getTargetResult());
                    targetResult.append("*");
                } else {
                    super.markupText(sourceType, source, targetType, target);
                }
            } else {
                super.markupText(sourceType, source, targetType, target);
            }
        }

    }

    private static class IncrementalWordsDiffMarker extends StringDiffMarker {
        public void markupText(DiffType sourceType, CharSequence source, DiffType targetType, CharSequence target) {
            if (DiffType.REMOVED.equals(sourceType) && targetType.equals(DiffType.ADDED)) {
                // Compare chars
                CharsDiffMarker marker = new CharsDiffMarker();
                Diff.compareChars(source.toString(), target.toString(), marker);

                if (marker.getAddedPercent() < 0.5 && marker.getRemovePercent() < 0.5) {
                    sourceResult.append("*");
                    sourceResult.append(marker.getSourceResult());
                    sourceResult.append("*");
                    targetResult.append("*");
                    targetResult.append(marker.getTargetResult());
                    targetResult.append("*");
                } else {
                    super.markupText(sourceType, source, targetType, target);
                }
            } else {
                super.markupText(sourceType, source, targetType, target);
            }
        }
    }
}
