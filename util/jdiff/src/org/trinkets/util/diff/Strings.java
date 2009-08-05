package org.trinkets.util.diff;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Strings utility.
 *
 * @author Alexey Efimov
 */
final class Strings {
    private Strings() {
    }

    public static String[] split(String text, String separators) {
        List<String> strings = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(text, separators, true);
        while (tokenizer.hasMoreTokens()) {
            strings.add(tokenizer.nextToken());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public static String[] lines(String text) {
        List<String> result = new ArrayList<String>();
        String[] lines = split(text, "\n\r");
        for (String line : lines) {
            if (result.size() > 0 && isLineSeparators(line) &&
                !result.get(result.size() - 1).endsWith("\n") && !result.get(result.size() - 1).endsWith("\r")) {
                result.set(result.size() - 1, result.get(result.size() - 1) + line);
            } else {
                result.add(line);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public static String[] words(String text) {
        return split(text, " \n\t\r\f.-_\\=,`\"~!@#$%^&*()[]{}<>\\|/+-?:;№");
    }

    public static Character[] toArray(char[] chars) {
        Character[] characters = new Character[chars.length];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = chars[i];
        }
        return characters;
    }

    public static Character[] toArray(CharSequence chars) {
        Character[] characters = new Character[chars.length()];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = chars.charAt(i);
        }
        return characters;
    }

    public static String[] lines(ByteBuffer byteBuffer, String encoding) {
        return lines(Charset.forName(encoding).decode(byteBuffer).toString());
    }

    public static boolean isLineSeparator(char c) {
        return c == '\n' || c == '\r';
    }

    public static boolean isLineSeparators(CharSequence c) {
        for (int i = 0; i < c.length(); i++) {
            if (!isLineSeparator(c.charAt(i))) {
                return false;
            }

        }
        return true;
    }

    public static char[] toArray(Character[] characters, int offset, int length) {
        char[] chars = new char[length];
        for (int i = offset; i < characters.length && i < offset + length; i++) {
            chars[i - offset] = characters[i];
        }
        return chars;
    }

    public static CharSequence toCharSequence(String[] strings, int offset, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < strings.length && i < offset + length; i++) {
            builder.append(strings[i]);
        }
        return builder;
    }
}
