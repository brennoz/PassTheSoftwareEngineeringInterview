package com.interview.algorithms.graphs.q098_dfs_traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {

    // O(V+E) recursive DFS — visited set prevents infinite loops on cycles
    public List<Integer> traverse(Node root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, result, new HashSet<>());
        return result;
    }

    private void dfs(Node node, List<Integer> result, Set<Node> visited) {
        if (visited.contains(node)) return;
        visited.add(node);
        result.add(node.getValue());
        for (Node neighbor : node.getNeighbors()) {
            dfs(neighbor, result, visited);
        }
    }

    public static class Node {
        private final int value;
        private final List<Node> neighbors = new ArrayList<>();

        public Node(int value) { this.value = value; }
        public int getValue() { return value; }
        public List<Node> getNeighbors() { return Collections.unmodifiableList(neighbors); }
        public void add(Node node) { neighbors.add(node); }
    }
}
