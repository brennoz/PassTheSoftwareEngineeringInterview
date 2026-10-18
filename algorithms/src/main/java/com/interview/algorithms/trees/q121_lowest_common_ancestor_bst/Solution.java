package com.interview.algorithms.trees.q121_lowest_common_ancestor_bst;

public class Solution {

    static class TreeNode {
        int val; TreeNode left; TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    // Algorithm: exploit BST ordering — both < root → go left; both > root → go right; else root is LCA
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode curr = root;
        while (curr != null) {
            if (Math.max(p.val, q.val) < curr.val) {
                curr = curr.left;
            } else if (Math.min(p.val, q.val) > curr.val) {
                curr = curr.right;
            } else {
                return curr;
            }
        }
        return null;
    }
}
