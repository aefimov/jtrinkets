package org.trinkets.util.diff;

/**
 * Strings marker.
 *
 * @author Alexey Efimov
 */
public class StringDiffMarker extends StringBuilderDiffMarker<String> {
    @Override
    protected CharSequence toCharSequence(String[] array, int offset, int length) {
        return Strings.toCharSequence(array, offset, length);
    }
}
