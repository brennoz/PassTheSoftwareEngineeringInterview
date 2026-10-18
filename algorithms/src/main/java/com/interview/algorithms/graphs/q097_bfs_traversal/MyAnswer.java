package com.interview.algorithms.graphs.q097_bfs_traversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyAnswer {

    public List<Integer> traverse(Node root) {
        throw new UnsupportedOperationException("not implemented");
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
