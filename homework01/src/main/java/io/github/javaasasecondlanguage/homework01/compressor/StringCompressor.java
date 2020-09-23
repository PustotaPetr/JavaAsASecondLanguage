package io.github.javaasasecondlanguage.homework01.compressor;

import java.util.Arrays;

public class StringCompressor {
    /**
     * Given an array of characters, compress it using the following algorithm:
     * <p>
     * Begin with an empty string s.
     * For each group of consecutive repeating characters in chars:
     * If the group's length is 1, append the character to s.
     * Otherwise, append the character followed by the group's length.
     * Return a compressed string.
     * </p>
     * Follow up:
     * Could you solve it using only O(1) extra space?
     * </p>
     * Examples:
     * a -> a
     * aa -> a2
     * aaa -> a3
     * aaabb -> a3b2
     * "" -> ""
     * null -> Illegal argument
     * 234 sdf -> Illegal argument
     *
     * @param str nullable array of chars to compress
     *            str may contain illegal characters
     * @return a compressed array
     * @throws IllegalArgumentException if str is null
     * @throws IllegalArgumentException if any char is not in range 'a'..'z'
     */
    public char[] compress(char[] str) {
        if (str == null) {
            throw new IllegalArgumentException("str is null");
        }
        if (str.length == 0) {
            return str;
        }

        char lastChar = str[0];
        int charCounter = 1;
        int compressedCharCounter = 0;
        char currentChar;

        for (int i = 1; i <= str.length; i++) {
            if (i == str.length) {
                currentChar = 0;
            } else {
                currentChar = str[i];
            }

            if (lastChar < 'a' || lastChar > 'z') {
                throw new IllegalArgumentException("char not in range 'a'..'z'");
            }
            if (lastChar != currentChar) {

                if (charCounter > 1) {
                    str[compressedCharCounter] = lastChar;
                    compressedCharCounter++;
                    for (char s : String.valueOf(charCounter).toCharArray()) {
                        str[compressedCharCounter] = s;
                        compressedCharCounter++;
                    }
                } else {
                    str[compressedCharCounter] = lastChar;
                    compressedCharCounter++;
                }
                lastChar = currentChar;
                charCounter = 1;
            } else {
                charCounter++;
            }
        }
        return Arrays.copyOfRange(str, 0, compressedCharCounter);
    }
}
