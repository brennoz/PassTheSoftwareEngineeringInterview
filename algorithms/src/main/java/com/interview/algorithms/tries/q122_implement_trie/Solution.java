package com.interview.algorithms.tries.q122_implement_trie;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    static class TrieNode {
        static final int ALPHABET_SIZE = 26;
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord = false;

        static int index(char c) { return c - 'a'; }
    }

    private final TrieNode root = new TrieNode();

    // Algorithm: walk character by character; create nodes lazily; mark end
    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int i = TrieNode.index(c);
            if (curr.children[i] == null) curr.children[i] = new TrieNode();
            curr = curr.children[i];
        }
        curr.isEndOfWord = true;
    }

    // Algorithm: walk to end of word; true only if that node has isEndOfWord set
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    // Algorithm: same walk as search but no isEndOfWord check needed
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    // Algorithm: navigate to prefix node, then DFS collecting all complete words below
    public List<String> findAllWithPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode prefixNode = findNode(prefix);
        if (prefixNode == null) return results;
        collectWords(prefixNode, new StringBuilder(prefix), results);
        return results;
    }

    private void collectWords(TrieNode node, StringBuilder current, List<String> results) {
        if (node.isEndOfWord) results.add(current.toString());
        for (int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
            if (node.children[i] != null) {
                current.append((char) ('a' + i));
                collectWords(node.children[i], current, results);
                current.deleteCharAt(current.length() - 1); // backtrack
            }
        }
    }

    private TrieNode findNode(String str) {
        TrieNode curr = root;
        for (char c : str.toCharArray()) {
            int i = TrieNode.index(c);
            if (curr.children[i] == null) return null;
            curr = curr.children[i];
        }
        return curr;
    }
}
