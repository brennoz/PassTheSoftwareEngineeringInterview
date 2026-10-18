package com.interview.algorithms.backtracking.q091_sudoku_solver;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    private static final int BOARD_LENGTH = 9;

    // Backtracking with three HashMaps per row/col/box for O(1) constraint checks
    @SuppressWarnings("unchecked")
    public boolean sudokuSolver(int[][] board) {
        Map<Integer, Boolean>[] rowsMap = new HashMap[BOARD_LENGTH];
        Map<Integer, Boolean>[] colsMap = new HashMap[BOARD_LENGTH];
        Map<Integer, Boolean>[] boxesMap = new HashMap[BOARD_LENGTH];
        for (int i = 0; i < BOARD_LENGTH; i++) {
            rowsMap[i] = new HashMap<>();
            colsMap[i] = new HashMap<>();
            boxesMap[i] = new HashMap<>();
        }
        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] != 0) {
                    int v = board[row][col];
                    int b = boxId(row, col);
                    rowsMap[row].put(v, true);
                    colsMap[col].put(v, true);
                    boxesMap[b].put(v, true);
                }
            }
        }
        return solve(board, rowsMap, colsMap, boxesMap, 0, 0);
    }

    private boolean solve(int[][] board, Map<Integer, Boolean>[] rowsMap, Map<Integer, Boolean>[] colsMap,
                          Map<Integer, Boolean>[] boxesMap, int row, int col) {
        if (row == BOARD_LENGTH) return true;
        int nextCol = col + 1;
        int nextRow = row;
        if (nextCol == BOARD_LENGTH) { nextCol = 0; nextRow++; }
        if (board[row][col] != 0) return solve(board, rowsMap, colsMap, boxesMap, nextRow, nextCol);
        int b = boxId(row, col);
        for (int num = 1; num <= BOARD_LENGTH; num++) {
            if (!rowsMap[row].containsKey(num) && !colsMap[col].containsKey(num) && !boxesMap[b].containsKey(num)) {
                board[row][col] = num;
                rowsMap[row].put(num, true);
                colsMap[col].put(num, true);
                boxesMap[b].put(num, true);
                if (solve(board, rowsMap, colsMap, boxesMap, nextRow, nextCol)) return true;
                rowsMap[row].remove(num);
                colsMap[col].remove(num);
                boxesMap[b].remove(num);
                board[row][col] = 0;
            }
        }
        return false;
    }

    // boxId = (row/3)*3 + (col/3) — maps (row,col) to one of 9 boxes 0..8
    private int boxId(int row, int col) {
        return Math.floorDiv(row, 3) * 3 + Math.floorDiv(col, 3);
    }
}
