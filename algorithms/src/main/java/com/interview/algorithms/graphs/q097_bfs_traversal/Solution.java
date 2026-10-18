package com.interview.algorithms.graphs.q097_bfs_traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Solution {

    // O(V+E) — Queue gives level-by-level traversal; visited set prevents revisiting
    public List<Integer> traverse(Node root) {
        List<Integer> result = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        visited.add(root);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.add(current.getValue());
            for (Node neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return result;
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
