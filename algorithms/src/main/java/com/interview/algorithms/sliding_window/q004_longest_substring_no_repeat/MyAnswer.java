package com.interview.algorithms.sliding_window.q004_longest_substring_no_repeat;

import java.util.HashSet;
import java.util.Set;

public class MyAnswer {

    public static void main (String[] args) {
        MyAnswer my = new MyAnswer();
        /*
        Input:  "abcabcbb"  → 3   ("abc")
        Input:  "bbbbb"     → 1   ("b")
        Input:  "pwwkew"    → 3   ("wke")
         */
        System.out.println("length: " + my.lengthOfLongestSubstring("abcabcbb"));
        System.out.println("length: " + my.lengthOfLongestSubstring("bbbbb"));
        System.out.println("length: " + my.lengthOfLongestSubstring("pwwkew"));
    }

    public int lengthOfLongestSubstring(String s) {
    
      Set<Character> sw = new HashSet<>();
      int l = 0, max = 0;
      for (int r = 0; r < s.length(); r++) {
          while (sw.contains(s.charAt(r))) {
              sw.remove(s.charAt(l++));
          }
          sw.add(s.charAt(r));
          max = Math.max(max, r - l + 1);  // r-l+1 beats sw.size() — O(1) vs O(N)
      }
      return max;

    }
}
