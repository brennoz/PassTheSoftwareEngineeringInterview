package com.interview.algorithms.graphs.q100_directed_acyclic_graph;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Solution {

    // Two maps for O(1) parent and child lookups without graph reversal
    private final Map<String, Set<String>> children = new HashMap<>();
    private final Map<String, Set<String>> parents  = new HashMap<>();

    public void addNode(String node) {
        children.computeIfAbsent(node, k -> new HashSet<>());
        parents.computeIfAbsent(node, k -> new HashSet<>());
    }

    public void addEdge(String parent, String child) {
        if (parent.equals(child)) throw new IllegalArgumentException("Self-loop not allowed");
        addNode(parent);
        addNode(child);
        if (hasEdge(parent, child)) return;
        if (isReachable(child, parent)) throw new IllegalArgumentException("Cycle detected");
        children.get(parent).add(child);
        parents.get(child).add(parent);
    }

    public boolean hasEdge(String parent, String child) {
        return children.getOrDefault(parent, Collections.emptySet()).contains(child);
    }

    public Set<String> childrenOf(String node) {
        return children.getOrDefault(node, Collections.emptySet());
    }

    public Set<String> parentsOf(String node) {
        return parents.getOrDefault(node, Collections.emptySet());
    }

    public Set<String> descendantsOf(String node) {
        return traverse(node, true);
    }

    public Set<String> ancestorsOf(String node) {
        return traverse(node, false);
    }

    // Iterative DFS — downwards=true walks children; false walks parents
    private Set<String> traverse(String start, boolean downwards) {
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        Collection<String> first = downwards
                ? children.getOrDefault(start, Collections.emptySet())
                : parents.getOrDefault(start, Collections.emptySet());
        stack.addAll(first);
        while (!stack.isEmpty()) {
            String cur = stack.pop();
            if (!visited.add(cur)) continue;
            Collection<String> next = downwards
                    ? children.getOrDefault(cur, Collections.emptySet())
                    : parents.getOrDefault(cur, Collections.emptySet());
            stack.addAll(next);
        }
        return visited;
    }

    private boolean isReachable(String from, String to) {
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(from);
        while (!stack.isEmpty()) {
            String cur = stack.pop();
            if (!visited.add(cur)) continue;
            for (String next : children.getOrDefault(cur, Collections.emptySet())) {
                if (next.equals(to)) return true;
                stack.push(next);
            }
        }
        return false;
    }
}
