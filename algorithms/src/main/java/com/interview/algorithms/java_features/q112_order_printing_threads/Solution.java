package com.interview.algorithms.java_features.q112_order_printing_threads;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    // Shared monitor: turn tracks whose turn it is; notifyAll wakes all waiting threads
    static class Coordinator {
        private int turn = 0;
        private int round = 0;
        private final int n;
        private final int rounds;

        Coordinator(int n, int rounds) { this.n = n; this.rounds = rounds; }

        public synchronized boolean tryPrint(int id) throws InterruptedException {
            if (round >= rounds) { notifyAll(); return false; }
            while (round < rounds && turn != id) {
                wait();
                if (round >= rounds) { notifyAll(); return false; }
            }
            if (round >= rounds) { notifyAll(); return false; }
            System.out.println("round=" + round + " thread=" + id);
            turn = (turn + 1) % n;
            if (turn == 0) round++;
            notifyAll();
            return true;
        }
    }

    public void run(int n, int rounds) throws InterruptedException {
        Coordinator coord = new Coordinator(n, rounds);
        List<Thread> threads = new ArrayList<>();
        for (int id = 0; id < n; id++) {
            final int tid = id;
            Thread t = new Thread(() -> {
                try { while (coord.tryPrint(tid)) {} }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();
    }
}
