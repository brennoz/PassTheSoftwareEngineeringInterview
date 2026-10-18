package com.interview.algorithms.trees.q118_binary_tree_dfs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Solution {

    static class TreeNode {
        int val; TreeNode left; TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    // Pre-order: visit root, then left subtree, then right subtree
    public List<Integer> preOrderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }

    private void preOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);
        preOrder(node.left, result);
        preOrder(node.right, result);
    }

    // In-order: left subtree, then root, then right subtree
    public List<Integer> inOrderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inOrder(node.left, result);
        result.add(node.val);
        inOrder(node.right, result);
    }

    // Post-order: left subtree, then right subtree, then root
    public List<Integer> postOrderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    private void postOrder(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postOrder(node.left, result);
        postOrder(node.right, result);
        result.add(node.val);
    }

    // Pre-order iterative: push right before left so left is processed first
    public List<Integer> preOrderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);
            if (node.right != null) stack.push(node.right); // push right first
            if (node.left != null) stack.push(node.left);   // left processed next
        }
        return result;
    }

    // In-order iterative: go left until null, visit, then go right
    public List<Integer> inOrderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            result.add(curr.val);
            curr = curr.right;
        }
        return result;
    }
}
