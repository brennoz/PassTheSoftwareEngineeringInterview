package com.interview.algorithms.intervals.q007_merge_intervals;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyAnswerTest {

    private final MyAnswer solution = new MyAnswer();

    @Test
    void mergesOverlappingIntervals() {
        int[][] result = solution.merge(new int[][]{{1,3},{2,6},{8,10},{15,18}});
        assertThat(result).isDeepEqualTo(new int[][]{{1,6},{8,10},{15,18}});
    }

    @Test
    void mergesTouchingIntervals() {
        int[][] result = solution.merge(new int[][]{{1,4},{4,5}});
        assertThat(result).isDeepEqualTo(new int[][]{{1,5}});
    }
}
