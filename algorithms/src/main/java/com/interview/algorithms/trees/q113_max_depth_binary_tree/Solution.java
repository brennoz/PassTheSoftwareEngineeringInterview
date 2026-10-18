package com.interview.algorithms.trees.q113_max_depth_binary_tree;

import java.util.Stack;

public class Solution {

    static class TreeNode {
        int val; TreeNode left; TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    // Pair needed for iterative DFS: carry (node, depth) together on the stack
    static class Pair<K, V> {
        private K key; private V value;
        Pair(K key, V value) { this.key = key; this.value = value; }
        K getKey() { return key; }
        V getValue() { return value; }
    }

    // Algorithm: recursive DFS — return 1 + max(left depth, right depth)
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // Algorithm: iterative DFS using explicit stack of (node, depth) pairs
    public int maxDepthIterative(TreeNode root) {
        if (root == null) return 0;
        Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
        stack.push(new Pair<>(root, 1));
        int res = 0;
        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> pair = stack.pop();
            TreeNode node = pair.getKey();
            int depth = pair.getValue();
            if (node != null) {
                res = Math.max(res, depth);
                stack.push(new Pair<>(node.left, depth + 1));
                stack.push(new Pair<>(node.right, depth + 1));
            }
        }
        return res;
    }
}
