# q112 — Ordered Printing with N Threads

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Given `n` threads (IDs 0..n-1) and a number of `rounds`, make the threads print in strict order:

```
round=0 thread=0
round=0 thread=1
...
round=0 thread=n-1
round=1 thread=0
...
```

Repeat for `rounds` rounds.

**Requirements:**
- Use `synchronized` + `wait()` / `notifyAll()` (no busy-waiting).
- No external libraries.

---

## Your task

Edit `MyAnswer.java` — implement the `Coordinator.tryPrint(int id)` method.
The outer thread-launch loop is provided — only the monitor logic needs implementing.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
