package com.interview.algorithms.stack.q110_stack_without_array;

public class MyAnswer {

    public void push(int val) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int pop() {
        throw new UnsupportedOperationException("not implemented");
    }

    public int peek() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("not implemented");
    }

    private static class Node {
        int val;
        Node next;
        Node(int val, Node next) { this.val = val; this.next = next; }
    }
}
