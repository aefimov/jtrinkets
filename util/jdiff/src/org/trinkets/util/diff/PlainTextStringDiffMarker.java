package org.trinkets.util.diff;

/**
 * Plain text strings marker.
 *
 * @author Alexey Efimov
 */
public class PlainTextStringDiffMarker extends PlainTextDiffMarker<String> {
    @Override
    protected CharSequence toCharSequence(String[] array, int offset, int length) {
        return Strings.toCharSequence(array, offset, length);
    }
}
