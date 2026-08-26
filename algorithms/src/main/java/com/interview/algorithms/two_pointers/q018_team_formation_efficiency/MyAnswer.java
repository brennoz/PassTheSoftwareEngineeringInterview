package com.interview.algorithms.two_pointers.q018_team_formation_efficiency;

import java.util.Arrays;

public class MyAnswer {

    public static void main(String[ ] args) {
        MyAnswer myAnswer = new MyAnswer();
        System.err.println("Result: " + myAnswer.dividePlayers(new int[] {1, 2, 3, 2}));
        System.err.println("Result: " + myAnswer.dividePlayers(new int[] {2, 6, 3, 0, 4, 3}));
        System.err.println("Result: " + myAnswer.dividePlayers(new int[] {2, 1, 1, 4, 3, 5}));
    }

    public long dividePlayers(int[] skill) {

        /*
            1   2   2   3
            L           R
                L   R

            0   2   3   3   4   6
            L                   R
                L           R
                    -   -
         */

        Arrays.sort(skill);

        int l = 0;
        int r = skill.length - 1;
        long sum = 0;
        // cleaner: no sentinel, intent is obvious
        int target = skill[l] + skill[r];
        while (l < r) {
            if (skill[l] + skill[r] != target) return -1;
            sum += (long) skill[l] * skill[r];
            l++; r--;
        }

        return sum;
    }
}
