package com.interview.algorithms.graphs.q099_dijkstra_shortest_path;

import java.util.ArrayList;
import java.util.List;

public class MyAnswer {

    public static final int UNREACHABLE = Integer.MAX_VALUE;

    private final int nodeCount;
    private final List<List<Edge>> adjacency;

    public MyAnswer(int nodeCount) {
        this.nodeCount = nodeCount;
        this.adjacency = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) adjacency.add(new ArrayList<>());
    }

    public void addEdge(int from, int to, int weight) {
        adjacency.get(from).add(new Edge(to, weight));
    }

    public void addBidirectionalEdge(int u, int v, int weight) {
        adjacency.get(u).add(new Edge(v, weight));
        adjacency.get(v).add(new Edge(u, weight));
    }

    public int[] shortestDistances(int source) {
        throw new UnsupportedOperationException("not implemented");
    }

    public List<Integer> shortestPath(int source, int target) {
        throw new UnsupportedOperationException("not implemented");
    }

    private record Edge(int to, int weight) {}
}
