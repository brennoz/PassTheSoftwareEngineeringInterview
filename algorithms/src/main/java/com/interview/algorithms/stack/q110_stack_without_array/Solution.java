package com.interview.algorithms.stack.q110_stack_without_array;

public class Solution {

    // Linked list stack: push prepends a new node; pop removes the head node
    private Node top;
    private int size;

    public void push(int val) {
        top = new Node(val, top);
        size++;
    }

    public int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        int val = top.val;
        top = top.next;
        size--;
        return val;
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return top.val;
    }

    public boolean isEmpty() { return top == null; }

    public int size() { return size; }

    private static class Node {
        int val;
        Node next;
        Node(int val, Node next) { this.val = val; this.next = next; }
    }
}
