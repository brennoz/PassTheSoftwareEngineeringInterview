# Hints — Q007 Merge Intervals

## Hint 1

**Sort first.** Once sorted by start time, you only ever need to compare the current interval against the LAST merged one — no need to look further back.

Two intervals overlap when: `current.start <= lastMerged.end`

If they overlap → extend: `lastMerged.end = max(lastMerged.end, current.end)`
If they don't → add current as new interval.

```
Sorted: [1,3] [2,6] [8,10] [15,18]

Take [1,3] → merged: [[1,3]]
Take [2,6] → 2 <= 3? YES → extend → merged: [[1,6]]
Take [8,10] → 8 <= 6? NO → add → merged: [[1,6],[8,10]]
Take [15,18] → 15 <= 10? NO → add → merged: [[1,6],[8,10],[15,18]]
```

Time: O(N log N) for sort + O(N) scan = O(N log N)
Space: O(N) for output list
