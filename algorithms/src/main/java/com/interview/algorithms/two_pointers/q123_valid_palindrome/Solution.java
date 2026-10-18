package com.interview.algorithms.two_pointers.q123_valid_palindrome;

public class Solution {

    // Algorithm: converging two pointers; inner while loops skip non-alphanumeric (not if — if only skips one)
    public boolean solve(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            while (l < r && !alphaNum(s.charAt(l))) l++;
            while (r > l && !alphaNum(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
            l++;
            r--;
        }
        return true;
    }

    public boolean alphaNum(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
