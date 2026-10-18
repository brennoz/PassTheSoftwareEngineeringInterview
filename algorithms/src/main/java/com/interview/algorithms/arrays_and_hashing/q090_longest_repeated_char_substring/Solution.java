package com.interview.algorithms.arrays_and_hashing.q090_longest_repeated_char_substring;

public class Solution {

    // O(n) single pass — track current run length, record max start position
    public static String longestRepeatedSubstring(String s) {
        if (s == null || s.isEmpty()) return "";
        int currentLength = 1;
        int maxLength = 1;
        int maxStart = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                currentLength++;
            } else {
                currentLength = 1;
            }
            if (currentLength > maxLength) {
                maxLength = currentLength;
                maxStart = i - currentLength + 1;
            }
        }
        return s.substring(maxStart, maxStart + maxLength);
    }
}
