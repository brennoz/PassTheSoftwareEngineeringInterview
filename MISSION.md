# Mission: Pass Senior Java Software Engineering Interviews

## Why
Pass all categories of software engineering interviews at senior level — OOD, Java concurrency,
system design, algorithms — with focus on fintech/trading domain (OMS, low-latency, event-driven).
Current target: TradingCo (IG Group) Senior Java Engineer — OMS, event sourcing, low latency.

## Success looks like
- OOD: given "design a bank/OMS/parking lot", produce clean class diagram + Java skeleton in 25 min
- Concurrency: explain and correctly implement ExecutorService, ForkJoinPool, CompletableFuture,
  thread safety patterns (`@Version`, `synchronized`, `AtomicLong`, `ConcurrentHashMap`)
- System design: explain event sourcing + CQRS tradeoffs with concrete numbers
- Code review: spot thread safety, JPA N+1, BigDecimal, missing idempotency in legacy Java
- Name the correct pattern for each design decision and justify it

## Constraints
- Interview simulation context — Java 21, no Spring unless specifically asked
- Each lesson completable in < 20 minutes
- Domain grounding: payments, banking, sport betting, trading OMS

## Learning tracks
| Track | Status |
|---|---|
| OOD — Bank system | In progress (Lesson 01 done) |
| Java Concurrency — ExecutorService, ForkJoin, CompletableFuture | Starting |
| System Design — Event Sourcing + CQRS | Whiteboard lessons done |
| Algorithms | Ongoing via question practice |
