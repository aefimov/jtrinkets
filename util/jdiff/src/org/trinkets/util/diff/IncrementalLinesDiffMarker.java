package org.trinkets.util.diff;

/**
 * Incremental lines plain text marker.
 *
 * @author Alexey Efimov
 */
public class IncrementalLinesDiffMarker extends IncrementalDiffMarker<String> {
    public IncrementalLinesDiffMarker(StringBuilderDiffMarker<String> subMarker) {
        this(subMarker, 0.5, subMarker.decorator);
    }

    public IncrementalLinesDiffMarker(StringBuilderDiffMarker<String> subMarker, StringBuilderDiffMarkupDecorator decorator) {
        this(subMarker, 0.5, decorator);
    }

    public IncrementalLinesDiffMarker(StringBuilderDiffMarker<String> subMarker, double threshold, StringBuilderDiffMarkupDecorator decorator) {
        super(subMarker, threshold, decorator);
    }

    public IncrementalLinesDiffMarker(StringBuilderDiffMarker<String> subMarker, double threshold) {
        super(subMarker, threshold);
    }

    @Override
    protected void subCompare(CharSequence source, CharSequence target, DiffMarker<String> subMarker) {
        // Compare incrementally
        DiffMarkup.compareWords(source.toString(), target.toString(), subMarker);
    }
}
