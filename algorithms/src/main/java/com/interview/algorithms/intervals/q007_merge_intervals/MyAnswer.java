package com.interview.algorithms.intervals.q007_merge_intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAnswer {

    public static void main (String[] args) {
        MyAnswer my = new MyAnswer();
        int[][] intervals = new int[][] {{1,3},{2,6},{8,10},{15,18}};
        int[][] result = my.merge(intervals);

        for (int r = 0; r < result.length; r++) {
            System.out.println("[" + result[r][0] + "," + result[r][1] + "]");
        }
    }

    public int[][] merge(int[][] intervals) {
        /*
            1   2   3   4   5   6   7   8   9   10  11  12  13  14  15  16  17  18
            ---------
                -----------------
                                        ----------                  
                                                                    --------------
         */
        List<int[]> results = new ArrayList<>();
        //sort array by start interval
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int[] lastMerged = new int[] {0, 0};
        for (int i = 0; i < intervals.length; i++) {
            int[] current = intervals[i];
            if (results.isEmpty() || current[0] > lastMerged[1]) {
                results.add(current);
                lastMerged = current;
            } else {
                //The max matters — handles the case where one interval is fully inside another ([1,4],[2,3] → don't shrink end to 3).
                lastMerged[1] = Math.max(lastMerged[1], current[1]);
                //results.add(lastMerged);
            }
        }

        return results.toArray(new int[0][]);
    }
}
