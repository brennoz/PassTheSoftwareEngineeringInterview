package com.interview.algorithms.two_pointers.q018_team_formation_efficiency;

import java.util.Arrays;

public class Solution {

    // Two Pointers: sort + pair smallest with largest
    // If any pair sum != target → return -1
    // Time O(N log N) | Space O(1)
    public long dividePlayers(int[] skill) {
        Arrays.sort(skill);
        int n = skill.length;
        int target = skill[0] + skill[n - 1];
        long efficiency = 0;

        int l = 0, r = n - 1;
        while (l < r) {
            if (skill[l] + skill[r] != target) return -1;
            efficiency += (long) skill[l] * skill[r];
            l++;
            r--;
        }
        return efficiency;
    }
}
