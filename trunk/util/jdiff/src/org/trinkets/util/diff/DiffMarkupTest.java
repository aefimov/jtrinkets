package org.trinkets.util.diff;

import junit.framework.TestCase;

/**
 * Diff tests.
 *
 * @author Alexey Efimov
 */
public class DiffMarkupTest extends TestCase {
    private static void appendMarkers(DiffNode.Type sourceType, StringBuilder sourceResult, DiffNode.Type targetType, StringBuilder targetResult) {
        if (targetType.equals(DiffNode.Type.ADDED)) {
            targetResult.append("+");
        }
        if (sourceType.equals(DiffNode.Type.REMOVED)) {
            sourceResult.append("-");
        }
    }

    private static StringBuilderDiffMarker<Character> createCharacterMarker() {
        return new CharacterDiffMarker() {
            @Override
            protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                appendMarkers(sourceType, sourceResult, targetType, targetResult);
            }

            @Override
            protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                appendMarkers(sourceType, sourceResult, targetType, targetResult);
            }
        };
    }

    private static StringBuilderDiffMarker<String> createStringMarker() {
        return new StringDiffMarker() {
            @Override
            protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                appendMarkers(sourceType, sourceResult, targetType, targetResult);
            }

            @Override
            protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                appendMarkers(sourceType, sourceResult, targetType, targetResult);
            }
        };
    }

    private static void appendSubMarkers(StringBuilder sourceResult, StringBuilder targetResult) {
        targetResult.append("*");
        sourceResult.append("*");
    }

    private static StringBuilderDiffMarker<String> createIncrementalMarker() {
        return
            new IncrementalLinesDiffMarker(
                new IncrementalWordsDiffMarker(
                    createCharacterMarker(),
                    0.5) {
                    @Override
                    protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                        appendMarkers(sourceType, sourceResult, targetType, targetResult);
                    }

                    @Override
                    protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                        appendMarkers(sourceType, sourceResult, targetType, targetResult);
                    }

                    @Override
                    protected void beforeSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                        appendSubMarkers(sourceResult, targetResult);
                    }

                    @Override
                    protected void afterSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                        appendSubMarkers(sourceResult, targetResult);
                    }
                },
                0.5) {
                @Override
                protected void beforeMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                    appendMarkers(sourceType, sourceResult, targetType, targetResult);
                }

                @Override
                protected void afterMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                    appendMarkers(sourceType, sourceResult, targetType, targetResult);
                }

                @Override
                protected void beforeSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                    appendSubMarkers(sourceResult, targetResult);
                }

                @Override
                protected void afterSubMarkupText(DiffNode.Type sourceType, DiffNode.Type targetType) {
                    appendSubMarkers(sourceResult, targetResult);
                }
            };
    }

    public void testCompareCharacters() {
        StringBuilderDiffMarker<Character> marker = createCharacterMarker();
        DiffMarkup.compareChars("XMJYAUZ", "MZJAWXU", marker);

        assertEquals("-X-MJ-Y-AU-Z-", marker.getSourceResult());
        assertEquals("M+Z+JA+WX+U", marker.getTargetResult());
    }

    public void testCompareWords() {
        StringBuilderDiffMarker<String> marker = createStringMarker();
        DiffMarkup.compareWords(
            "The red brown fox jumped over the roling log",
            "The brown spotted fox leaped over the rolling log",
            marker
        );

        assertEquals("The -red -brown fox -jumped- over the -roling- log", marker.getSourceResult());
        assertEquals("The brown+ spotted+ fox +leaped+ over the +rolling+ log", marker.getTargetResult());
    }

    public void testCompareLines() {
        StringBuilderDiffMarker<String> marker = createStringMarker();
        DiffMarkup.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("-The red brown fox\n-" +
            "-jumped over the roling log-", marker.getSourceResult());
        assertEquals("+The brown spotted fox\n+" +
            "+leaped over the rolling log+", marker.getTargetResult());
    }

    public void testCompareLinesIncremental() {
        StringBuilderDiffMarker<String> marker = createIncrementalMarker();
        DiffMarkup.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("*The -red -brown fox\n*" +
            "*-jumped- over the *roling* log*", marker.getSourceResult());
        assertEquals("*The brown+ spotted+ fox\n*" +
            "*+leaped+ over the *rol+l+ing* log*", marker.getTargetResult());
    }

    public void testCompareComplexLinesIncremental() {
        StringBuilderDiffMarker<String> marker = createIncrementalMarker();
        DiffMarkup.compareLines(
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
        StringBuilderDiffMarker<String> marker = createIncrementalMarker();
        DiffMarkup.compareLines(
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n*\t<option name=\"Make\" -value-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n+\t<option name=\"AntTarget\" enabled=\"false\" />\n+" +
            "*\t<option name=\"Make\" +enabled+=\"true\" />\n*\tafter\n", marker.getTargetResult());
    }

    public void testOneUnchangedLineToTwoLines() {
        StringBuilderDiffMarker<String> marker = createIncrementalMarker();
        DiffMarkup.compareLines(
            "import java.util.List;\n\n" +
                "import org.springframework.beans.factory.annotation.Required;\n\n" +
                "import com.acme.util.Loggers;\n" +
                "import com.acme.MyObject;\n",
            "import java.util.List;\n\n" +
                "import org.apache.log4j.Logger;\n" +
                "import org.springframework.beans.factory.annotation.Required;\n\n" +
                "import com.acme.MyObject;\n",
            marker);

        assertEquals("import java.util.List;\n\n" +
            "import org.springframework.beans.factory.annotation.Required;\n" +
            "\n" +
            "-import com.acme.util.Loggers;\n-" +
            "import com.acme.MyObject;\n",
            marker.getSourceResult());
        assertEquals("import java.util.List;\n" +
            "\n" +
            "+import org.apache.log4j.Logger;\n+" +
            "import org.springframework.beans.factory.annotation.Required;\n\n" +
            "import com.acme.MyObject;\n",
            marker.getTargetResult());

    }

    public void testTwoLineToOneLine() {
        StringBuilderDiffMarker<String> marker = createIncrementalMarker();
        DiffMarkup.compareLines(
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n-\t<option name=\"AntTarget\" enabled=\"false\" />\n-" +
            "*\t<option name=\"Make\" -enabled-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n*\t<option name=\"Make\" +value+=\"true\" />\n*\tafter\n", marker.getTargetResult());
    }
}
