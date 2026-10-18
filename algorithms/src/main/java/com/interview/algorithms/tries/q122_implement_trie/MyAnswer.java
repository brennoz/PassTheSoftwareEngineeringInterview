package com.interview.algorithms.tries.q122_implement_trie;

import java.util.List;

public class MyAnswer {

    static class TrieNode {
        static final int ALPHABET_SIZE = 26;
        TrieNode[] children = new TrieNode[ALPHABET_SIZE];
        boolean isEndOfWord = false;

        static int index(char c) { return c - 'a'; }
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean search(String word) {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean startsWith(String prefix) {
        throw new UnsupportedOperationException("not implemented");
    }

    public List<String> findAllWithPrefix(String prefix) {
        throw new UnsupportedOperationException("not implemented");
    }
}
