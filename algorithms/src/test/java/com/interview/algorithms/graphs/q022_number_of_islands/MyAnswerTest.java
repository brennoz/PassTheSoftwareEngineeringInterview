package com.interview.algorithms.graphs.q022_number_of_islands;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyAnswerTest {

    private final MyAnswer answer = new MyAnswer();

    @Test
    void singleIsland() {
        char[][] grid = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        assertThat(answer.numIslands(grid)).isEqualTo(1);
    }

    @Test
    void threeIslands() {
        char[][] grid = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        assertThat(answer.numIslands(grid)).isEqualTo(3);
    }
}
