---
name: java-concurrency-executor-forkjoin
description: First Java concurrency lesson — ExecutorService vs ForkJoinPool, work stealing, RecursiveTask, commonPool starvation trap
metadata:
  type: project
---

# Java Concurrency — ExecutorService & ForkJoinPool

**Context:** New learning track opened for TradingCo (IG Group) interview prep — OMS, low-latency, event sourcing. Concurrency is load-bearing for OMS: parallel order validation + in-memory P&L aggregation.

**Key concepts introduced:**
- Raw threads don't scale: 1000 threads = ~1GB stack RAM, ~1s creation cost
- ExecutorService: thread pool for independent, uniform tasks — `submit()` → `Future`, `invokeAll()` for batches, always `shutdown()`
- ForkJoinPool: work-stealing pool for recursive divide-and-conquer CPU work
- Work-stealing: each thread has own deque; idle threads steal from top of busy deques
- RecursiveTask pattern: `fork()` left, `compute()` right on current thread, `join()` left — never fork both
- THRESHOLD: too small = scheduling overhead wins; too large = idle threads; aim ~100μs–10ms per task

**Key traps to remember for interview:**
- Forgetting `shutdown()` = JVM never exits (Java 19+ try-with-resources fixes this)
- Blocking I/O inside ForkJoinPool defeats work-stealing — use ExecutorService for I/O
- `CompletableFuture.supplyAsync()` with no executor uses `commonPool` — blocking call there starves all parallel streams in the JVM

**OMS grounding:**
- ExecutorService → parallel order validation (N independent tasks, may do DB/risk engine calls)
- ForkJoinPool → P&L aggregation over millions of positions (pure CPU, recursive array split)

**Next:** CompletableFuture pipelines — `thenApply` vs `thenCompose`, `allOf`, `exceptionally`, custom executor wiring
