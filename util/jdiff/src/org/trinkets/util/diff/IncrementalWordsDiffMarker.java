package org.trinkets.util.diff;

/**
 * Incremental words plain text marker.
 *
 * @author Alexey Efimov
 */
public class IncrementalWordsDiffMarker extends IncrementalDiffMarker<Character> {
    public IncrementalWordsDiffMarker(StringBuilderDiffMarker<Character> subMarker, double threshold) {
        super(subMarker, threshold);
    }

    @Override
    protected void subCompare(CharSequence source, CharSequence target, DiffMarker<Character> subMarker) {
        // Compare incrementally
        DiffMarkup.compareChars(source.toString(), target.toString(), subMarker);
    }
}
