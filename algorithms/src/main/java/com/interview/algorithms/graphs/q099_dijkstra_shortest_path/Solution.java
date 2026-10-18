package com.interview.algorithms.graphs.q099_dijkstra_shortest_path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Solution {

    public static final int UNREACHABLE = Integer.MAX_VALUE;

    private final int nodeCount;
    private final List<List<Edge>> adjacency;

    public Solution(int nodeCount) {
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

    // O((V+E) log V) — min-heap with lazy stale-entry deletion
    public int[] shortestDistances(int source) {
        int[] dist = new int[nodeCount];
        Arrays.fill(dist, UNREACHABLE);
        dist[source] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, source});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue; // stale entry
            for (Edge edge : adjacency.get(u)) {
                int newDist = dist[u] + edge.weight();
                if (newDist < dist[edge.to()]) {
                    dist[edge.to()] = newDist;
                    pq.offer(new int[]{newDist, edge.to()});
                }
            }
        }
        return dist;
    }

    // Dijkstra with predecessor array for path reconstruction
    public List<Integer> shortestPath(int source, int target) {
        int[] dist = new int[nodeCount];
        int[] prev = new int[nodeCount];
        Arrays.fill(dist, UNREACHABLE);
        Arrays.fill(prev, -1);
        dist[source] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, source});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;
            if (u == target) break;
            for (Edge edge : adjacency.get(u)) {
                int newDist = dist[u] + edge.weight();
                if (newDist < dist[edge.to()]) {
                    dist[edge.to()] = newDist;
                    prev[edge.to()] = u;
                    pq.offer(new int[]{newDist, edge.to()});
                }
            }
        }
        if (dist[target] == UNREACHABLE) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        for (int cur = target; cur != -1; cur = prev[cur]) path.add(cur);
        Collections.reverse(path);
        return path;
    }

    private record Edge(int to, int weight) {}
}
