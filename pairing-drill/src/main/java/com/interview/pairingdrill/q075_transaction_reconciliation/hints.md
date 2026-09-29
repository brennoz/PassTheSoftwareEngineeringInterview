# Hints — q075_transaction_reconciliation

## Hint 1 — where to start

Fastest way in: pick the "sometimes it throws right away" symptom first, not the hanging or
the missing-log ones — it's the most deterministic to reproduce, which makes it the safest
first characterization test to write.

Look at `rateFor`:
```java
Optional<BigDecimal> rate = Optional.of(rateProvider.getRate(tx.getCurrency()));
return rate.get();
```

Two questions worth asking out loud before touching it:
1. What does `Optional.of(x)` do, specifically, when `x` is `null`? (Not `Optional.empty()` —
   check what actually happens.)
2. Look at `RateProvider.getRate`'s signature and its one-line comment. Does the interface
   promise this method never returns `null`?

That combination is the first bug. The other two symptoms ("hangs" and "missing log entry, no
error") are separate — don't assume fixing this one explains all three.
