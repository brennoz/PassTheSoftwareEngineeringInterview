package com.interview.prep.natwest.ex4_library_borrowing.solution;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// CopyOnWriteArrayList, not ArrayList: borrow/return can happen from concurrent callers
// (Part B), and this list is written to on every one of them. A plain ArrayList would risk
// a ConcurrentModificationException or a lost add() under exactly the same kind of race
// BookCopy.tryBorrow() protects against — cheaper to reach for a concurrent collection here
// than to hand-roll a lock for a simple append-only log.
public class InMemoryAuditTrail implements AuditTrail {

    private final List<String> events = new CopyOnWriteArrayList<>();

    @Override
    public void record(String event) {
        events.add(event);
    }

    @Override
    public List<String> allEvents() {
        return Collections.unmodifiableList(events);
    }
}
