package org.trinkets.util.diff;

/**
 * Characters diff marker.
 *
 * @author Alexey Efimov
 */
public class CharacterDiffMarker extends StringBuilderDiffMarker<Character> {
    @Override
    protected CharSequence toCharSequence(Character[] array, int offset, int length) {
        return new String(Strings.toArray(array, offset, length));
    }
}
