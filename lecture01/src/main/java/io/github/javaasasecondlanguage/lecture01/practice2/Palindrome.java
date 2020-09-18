package io.github.javaasasecondlanguage.lecture01.practice2;

public class Palindrome {
    /**
     * @param str - Nullable string
     * @return True if str is a palindrome
     * 1. Empty string is a palindrome.
     * 2. "aba" is a palindrome.
     * 3. "ab" is not a palindrome.
     * 4. "abA" is not a palindrome.
     * @throws IllegalArgumentException if str is `null`
     */
    public static boolean isPalindrome(String str) {
        if (str == null) throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder(str);
        return str.equals(sb.reverse().toString());
    }
}
