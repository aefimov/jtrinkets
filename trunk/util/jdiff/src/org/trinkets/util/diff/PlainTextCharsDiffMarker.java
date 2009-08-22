package org.trinkets.util.diff;

/**
 * Plain text chars diff marker.
 *
 * @author Alexey Efimov
 */
public class PlainTextCharsDiffMarker extends PlainTextDiffMarker<Character> {
    @Override
    protected CharSequence toCharSequence(Character[] array, int offset, int length) {
        return new String(Strings.toArray(array, offset, length));
    }
}
