package com.interview.algorithms.sliding_window.q108_longest_repeating_char_replacement;

public class Solution {

    // O(n) — lazy maxFreq high-water mark: only increments when dominant freq grows, never decrements on shrink
    public int solve(String s, int k) {
        int[] count = new int[26];
        int l = 0, maxFreq = 0, res = 0;
        for (int r = 0; r < s.length(); r++) {
            count[s.charAt(r) - 'A']++;
            maxFreq = Math.max(maxFreq, count[s.charAt(r) - 'A']);
            // Validity: (windowSize - maxFreq) is the minimum replacements needed
            while ((r - l + 1) - maxFreq > k) {
                count[s.charAt(l) - 'A']--;
                l++;
            }
            res = Math.max(res, r - l + 1);
        }
        return res;
    }
}
