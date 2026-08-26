# Review — q060 top_clean_transactions

**Result: ❌ Fail**

## Bug 1 (critical) — wrong partition returned

```java
List<Transaction> flaggedTxs = groupedByFlag.get(Boolean.TRUE);
```

Spec: return top-N **unflagged/clean** transactions. This pulls `Boolean.TRUE` — the flagged
bucket — not `Boolean.FALSE`. Verified empirically against the spec's own example
(t1 flagged 9000, t2/t3/t4/t5 unflagged 450/1200/1200/75, topN=3):

- Expected: `[t3, t4, t2]` (unflagged, sorted desc, tie broken by id)
- Actual: `[t1]` (the one flagged transaction — wrong bucket entirely, everything else silently dropped)

Not a typo in isolation — `MyAnswerTest.java`'s two tests are both named
`should_return_N_flagged_transactions` and assert flagged ids come back. Implementation and
tests agree with each other, both backwards against the spec: "flagged for review" was read as
"the interesting ones to surface" rather than "the ones already excluded from consideration."

## Bug 2 — early-return branch skips sorting

```java
if (!flaggedTxs.isEmpty() && flaggedTxs.size() < topN) {
    return flaggedTxs;
}
```

When the bucket has fewer than `topN` entries, this returns it as-is — in original list
encounter order, never sorted. Spec requires descending-by-amount ordering unconditionally,
capped-or-not. Verified: 2 transactions (amount 100 then 500, in that list order), topN=5 →
returned `[100, 500]` instead of `[500, 100]`.

Independent of bug 1 — fixing the bucket selection alone would still leave this branch
returning unsorted results whenever the clean-transaction count is under `topN`. Neither of
the submitted tests exercises this path: both use `topN=3`/`topN=4` against a 4-flagged-out-of-7
list, so the bucket size (4) never drops below `topN`, and the buggy branch is never taken by
either test — same shape as q056's gap: tests pass, but they were built to avoid the exact
input that would expose the bug.

## What passed

- `Comparator.comparing(Transaction::amount).reversed().thenComparing(Transaction::id)` —
  correctly scoped, `reversed()` applies only to the amount comparison, tiebreaker is a real
  second comparator. Mechanically correct once it's running against the right data.
- `Collectors.partitioningBy` used as intended (not a manual loop) — right tool, wrong key
  read off the result map.

## Fix

```java
List<Transaction> cleanTxs = groupedByFlag.get(Boolean.FALSE);

return cleanTxs.stream()
        .sorted(Comparator.comparing(Transaction::amount).reversed().thenComparing(Transaction::id))
        .limit(topN)
        .toList();
```

Drop the early-return branch entirely — `sorted().limit(topN)` already handles "fewer than
topN available" correctly (`limit` on a shorter stream just returns everything), so the special
case was unnecessary as well as buggy.

## Retry

Reread the requirement — "top clean transactions" means the ones NOT flagged. When retrying,
write a test first with a flagged transaction that has the highest amount in the list, and
assert it's absent from the result — that's the one case that would have caught bug 1
immediately.
