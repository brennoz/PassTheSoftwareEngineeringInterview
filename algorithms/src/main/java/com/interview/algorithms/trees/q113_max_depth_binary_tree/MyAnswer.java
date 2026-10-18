package com.interview.algorithms.trees.q113_max_depth_binary_tree;

public class MyAnswer {

    static class TreeNode {
        int val; TreeNode left; TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    static class Pair<K, V> {
        private K key; private V value;
        Pair(K key, V value) { this.key = key; this.value = value; }
        K getKey() { return key; }
        V getValue() { return value; }
    }

    public int maxDepth(TreeNode root) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int maxDepthIterative(TreeNode root) {
        throw new UnsupportedOperationException("not implemented");
    }
}
