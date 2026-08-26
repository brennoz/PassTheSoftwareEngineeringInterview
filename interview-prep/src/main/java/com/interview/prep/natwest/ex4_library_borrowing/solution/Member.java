package com.interview.prep.natwest.ex4_library_borrowing.solution;

import java.util.ArrayList;
import java.util.List;

// Why Member owns its own borrowed-copy list rather than Library computing "what does this
// member have out" by scanning all copies: it's the same information-expert principle as the
// rest of this course's OOD questions — the object that HAS the data (which copies this
// member is holding) is the object that should answer questions about it, not a service class
// reaching into another object's internals to derive the same answer by brute force.
public class Member {

    private final String id;
    private final String name;
    private final List<String> borrowedCopyIds = new ArrayList<>();

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void recordBorrow(String copyId) {
        borrowedCopyIds.add(copyId);
    }

    void recordReturn(String copyId) {
        borrowedCopyIds.remove(copyId);
    }

    boolean hasBorrowed(String copyId) {
        return borrowedCopyIds.contains(copyId);
    }

    public List<String> getBorrowedCopyIds() {
        return List.copyOf(borrowedCopyIds);
    }
}
