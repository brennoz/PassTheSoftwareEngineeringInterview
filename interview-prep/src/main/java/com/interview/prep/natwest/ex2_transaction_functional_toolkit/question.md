# Ex2 — TransactionFunctionalToolkit

**Round:** NatWest — 2nd interview (live technical coding exercise)
**Topics:** `CompletableFuture.thenApply` vs `thenCompose`, RxJava `Observable` (`map`/`filter`/`reduce`), `Stream.reduce` vs `Collectors`
**Time budget:** ~15–20 minutes

---

## Scenario

Three small, independent problems, all from the interviewer's flagged topic list. Treat each as its own mini-exercise — write a test first for each, then implement.

## Part A — thenApply vs thenCompose

Given an `AccountFetcher.fetchAccount(String accountId)` that returns `CompletableFuture<Account>`:

1. Write `fetchBalance(String accountId)` returning `CompletableFuture<BigDecimal>` — a **pure transform** of the fetched account (no further I/O).
2. Given a `TransactionValidator.validate(Account, Transaction)` that **also** returns a `CompletableFuture<ValidationResult>` (imagine it's a downstream fraud-check API call), write `validateTransaction(Transaction transaction)` returning `CompletableFuture<ValidationResult>` — chaining the account fetch into the validation call.

Be ready to explain out loud: why does (1) use one composition method and (2) the other? What would go wrong (types, not just style) if you used the wrong one in either case?

## Part B — RxJava Observable

Given an `Observable<BigDecimal>` of incoming transaction amounts:

1. Compute the total of all **valid** transactions — valid means positive and under some maximum single-transaction threshold — using `filter` + `reduce` (not a manual loop, not `blockingGet` inside the pipeline itself).
2. Separately, write a method that maps each amount to a formatted description string.

## Part C — reduce vs Collectors, and String.reduce

1. Given `List<String> parts`, join them with `"-"` two ways: once using `Stream.reduce`, once using `Stream.collect(Collectors.joining("-"))`. Call both with an **empty list** and note — out loud — the actual difference in what comes back.
2. Compute a simple checksum of a String by reducing over its characters (`String.chars()` gives you an `IntStream`).

## Constraints

- No framework — plain Java 21, RxJava 3 (already a dependency of this module — `io.reactivex.rxjava3:rxjava`), JUnit 5 + AssertJ.
- Write each test before its implementation.

## What a strong answer demonstrates

- Correctly states the rule: `thenApply` for a synchronous transform, `thenCompose` when the next step itself returns a `CompletableFuture` (avoids `CompletableFuture<CompletableFuture<T>>`).
- RxJava operator vocabulary used correctly — `filter`/`map`/`reduce` composed declaratively, not devolved into an imperative subscribe-and-mutate-a-variable pattern.
- Can articulate the reduce-vs-collect distinction precisely: `reduce` with no identity returns `Optional` (no result for empty input); `Collectors.joining` is a mutable reduction purpose-built for strings and never needs `Optional`.

## If you get stuck

Full reference in `solution/` — `AccountFetcher`, `TransactionValidator`, `TransactionPipeline` (Part A), `TransactionStreamProcessor` (Part B), `ReferenceBuilder` (Part C), `ToolkitMain` runnable demo, and `SolutionTest`. Write your own attempt in `answer/`.
