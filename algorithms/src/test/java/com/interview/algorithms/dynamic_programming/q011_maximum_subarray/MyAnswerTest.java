package com.interview.algorithms.dynamic_programming.q011_maximum_subarray;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyAnswerTest {

    private final MyAnswer solution = new MyAnswer();

    @Test
    void returnsMaxSubarraySum() {
        assertThat(solution.maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4})).isEqualTo(6);
    }

    @Test
    void allNegativeReturnsLeastNegative() {
        assertThat(solution.maxSubArray(new int[]{-1,-2,-3})).isEqualTo(-1);
    }
}
