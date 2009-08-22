package org.trinkets.util.diff;

import junit.framework.TestCase;

/**
 * Diff tests.
 *
 * @author Alexey Efimov
 */
public class DiffMarkupTest extends TestCase {
    public void testCompareCharacters() {
        PlainTextCharsDiffMarker marker = new PlainTextCharsDiffMarker();
        DiffMarkup.compareChars("XMJYAUZ", "MZJAWXU", marker);

        assertEquals("-X-MJ-Y-AU-Z-", marker.getSourceResult());
        assertEquals("M+Z+JA+WX+U", marker.getTargetResult());
    }

    public void testCompareWords() {
        PlainTextStringDiffMarker marker = new PlainTextStringDiffMarker();
        DiffMarkup.compareWords(
            "The red brown fox jumped over the roling log",
            "The brown spotted fox leaped over the rolling log",
            marker
        );

        assertEquals("The -red -brown fox -jumped- over the -roling- log", marker.getSourceResult());
        assertEquals("The brown+ spotted+ fox +leaped+ over the +rolling+ log", marker.getTargetResult());
    }

    public void testCompareLines() {
        PlainTextStringDiffMarker marker = new PlainTextStringDiffMarker();
        DiffMarkup.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("-The red brown fox\n-" +
            "-jumped over the roling log-", marker.getSourceResult());
        assertEquals("+The brown spotted fox\n+" +
            "+leaped over the rolling log+", marker.getTargetResult());
    }

    public void testCompareLinesIncremental() {
        PlainTextIncrementalLinesDiffMarker marker = new PlainTextIncrementalLinesDiffMarker();
        DiffMarkup.compareLines("The red brown fox\n" +
            "jumped over the roling log", "The brown spotted fox\n" +
            "leaped over the rolling log", marker);

        assertEquals("*The -red -brown fox\n*" +
            "*-jumped- over the *roling* log*", marker.getSourceResult());
        assertEquals("*The brown+ spotted+ fox\n*" +
            "*+leaped+ over the *rol+l+ing* log*", marker.getTargetResult());
    }

    public void testCompareComplexLinesIncremental() {
        PlainTextIncrementalLinesDiffMarker marker = new PlainTextIncrementalLinesDiffMarker();
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
        PlainTextIncrementalLinesDiffMarker marker = new PlainTextIncrementalLinesDiffMarker();
        DiffMarkup.compareLines(
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n*\t<option name=\"Make\" -value-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n+\t<option name=\"AntTarget\" enabled=\"false\" />\n+" +
            "*\t<option name=\"Make\" +enabled+=\"true\" />*\n\tafter\n", marker.getTargetResult());
    }

    public void testOneUnchangedLineToTwoLines() {
        PlainTextIncrementalLinesDiffMarker marker = new PlainTextIncrementalLinesDiffMarker();
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
        PlainTextIncrementalLinesDiffMarker marker = new PlainTextIncrementalLinesDiffMarker();
        DiffMarkup.compareLines(
            "\tbefore\n\t<option name=\"AntTarget\" enabled=\"false\" />\n\t<option name=\"Make\" enabled=\"true\" />\n\tafter\n",
            "\tbefore\n\t<option name=\"Make\" value=\"true\" />\n\tafter\n",
            marker);

        assertEquals("\tbefore\n-\t<option name=\"AntTarget\" enabled=\"false\" />\n-" +
            "*\t<option name=\"Make\" -enabled-=\"true\" />\n*\tafter\n", marker.getSourceResult());
        assertEquals("\tbefore\n*\t<option name=\"Make\" +value+=\"true\" />\n*\tafter\n", marker.getTargetResult());
    }
}
