package org.trinkets.util.diff;

import junit.framework.TestCase;

/**
 * Diff tests.
 *
 * @author Alexey Efimov
 */
public class DiffTest extends TestCase {
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

        assertEquals("-The red brown fox\n-" +
            "-jumped over the roling log-", marker.getSourceResult());
        assertEquals("+The brown spotted fox\n+" +
            "+leaped over the rolling log+", marker.getTargetResult());
    }

    public void testCompareLinesIncremental() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("*The -red -brown fox\n*" +
            "*-jumped- over the *roling* log*", marker.getSourceResult());
        assertEquals("*The brown+ spotted+ fox\n*" +
            "*+leaped+ over the *rol+l+ing* log*", marker.getTargetResult());
    }

    public void testCompareComplexLinesIncremental() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines(
            "aaa\n\nbbb\nccc\n\nddd\n\neee\nfff\nggg\n",
            "aaa\n\nzzz\nbbb\n\nxxx\nddd\n\nfff\nggg\nhhh\n", marker);
        assertEquals("aaa\n" +
            "\n" +
            "bbb\n" +
            "-ccc\n-" +
            "\n" +
            "ddd\n" +
            "\n" +
            "-eee\n-" +
            "fff\n" +
            "ggg\n", marker.getSourceResult());
        assertEquals("aaa\n" +
            "\n" +
            "+zzz\n" +
            "+bbb\n" +
            "\n" +
            "+xxx\n+" +
            "ddd\n" +
            "\n" +
            "fff\n" +
            "ggg\n+" +
            "hhh\n+", marker.getTargetResult());
    }

    public void testOneLineToTwoLines() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines(
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n*\t<option name=\"Make\" -value-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n+\t<option name=\"AntTarget\" enabled=\"false\" />\n+" +
            "*\t<option name=\"Make\" +enabled+=\"true\" />*\n\tafter\n", marker.getTargetResult());
    }

    public void testOneUnchangedLineToTwoLines() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines(
            "import java.util.List;\n\n" +
                "import org.springframework.beans.factory.annotation.Required;\n\n" +
                "import ru.yandex.devboard.util.Loggers;\n" +
                "import ru.yandex.misc.worker.DelayingWorkerThread;\n",
            "import java.util.List;\n\n" +
                "import org.apache.log4j.Logger;\n" +
                "import org.springframework.beans.factory.annotation.Required;\n\n" +
                "import ru.yandex.misc.worker.DelayingWorkerThread;\n",
            marker);

        assertEquals("import java.util.List;\n\n" +
            "import org.springframework.beans.factory.annotation.Required;\n" +
            "\n" +
            "-import ru.yandex.devboard.util.Loggers;\n-" +
            "import ru.yandex.misc.worker.DelayingWorkerThread;\n",
            marker.getSourceResult());
        assertEquals("import java.util.List;\n" +
            "\n" +
            "+import org.apache.log4j.Logger;\n+" +
            "import org.springframework.beans.factory.annotation.Required;\n\n" +
            "import ru.yandex.misc.worker.DelayingWorkerThread;\n",
            marker.getTargetResult());

    }

    public void testTwoLineToOneLine() {
        IncrementalLinesDiffMarker marker = new IncrementalLinesDiffMarker();
        Diff.compareLines(
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n-\t<option name=\"AntTarget\" enabled=\"false\" />\n-" +
            "*\t<option name=\"Make\" -enabled-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n*\t<option name=\"Make\" +value+=\"true\" />\n*\tafter\n", marker.getTargetResult());
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
