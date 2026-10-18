package com.interview.algorithms.java_features.q111_finite_state_machine;

public class Solution {

    // FSM: 0=initial, 1=saw@, 2=in digits, 3=match; unexpected char resets to 0
    public boolean match(String s) {
        int state = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (state == 0) {
                if (c == '@') state = 1;
            } else if (state == 1) {
                if (Character.isDigit(c)) state = 2;
                else if (c == '@') state = 1;
                else state = 0;
            } else if (state == 2) {
                if (c == '#') state = 3;
                else if (Character.isDigit(c)) { /* stay in 2 */ }
                else if (c == '@') state = 1;
                else state = 0;
            }
        }
        return state == 3;
    }
}
