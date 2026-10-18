package com.interview.algorithms.trees.q120_validate_binary_search_tree;

public class Solution {

    static class TreeNode {
        int val; TreeNode left; TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    public boolean isValidBST(TreeNode root) {
        return valid(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    // Algorithm: pass bounds down the tree; Long used to safely handle Integer.MIN/MAX_VALUE nodes
    private boolean valid(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return valid(node.left, min, node.val) && valid(node.right, node.val, max);
    }
}
