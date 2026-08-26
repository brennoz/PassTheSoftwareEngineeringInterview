# Exercise — Top Spender Per Department (Medium)

**Concepts:** nested `Collectors.groupingBy`, `Collectors.collectingAndThen`,
`Collectors.reducing`, `Map.Entry.comparingByValue`, imperative vs functional trade-offs
**Estimated time:** 30 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java` for a side-by-side loop-based version once you've attempted
your own solution

---

## Problem

Given `Purchase(String employeeName, String department, BigDecimal amount)` — an employee may
appear multiple times in the same department (multiple purchases) — implement, in
`MyAnswer.java`:

```java
public static Map<String, String> topSpenderPerDepartment(List<Purchase> purchases)
```

For each department, sum every employee's purchases IN THAT DEPARTMENT, then return the name
of whichever employee's total is highest. Result: `department -> employeeName`. Empty input →
empty map.

## Why this is more than "just call groupingBy"

This needs **two levels** of aggregation, not one:
1. Within a department, purchases need to be summed **per employee** first (an employee with
   three small purchases might out-total someone with one big purchase).
2. Then, across those per-employee totals, you need to find the max.

A single flat `groupingBy(Purchase::department, ...)` can't do both steps in one collector
call — you need a downstream collector that is ITSELF the result of composing more than one
`Collectors` call together.

## Key questions before coding

- If you nest `Collectors.groupingBy` as the downstream of an outer `groupingBy`, what type do
  you end up with for each department — and how do you get from "a map of employee → total"
  down to "the single employee name with the highest total," without leaving the collector
  pipeline to write a manual loop?
- `Collectors.collectingAndThen(downstream, finisher)` exists for exactly this — applying one
  more transformation to a downstream collector's result before it's stored in the outer map.
  What would `finisher` need to do here?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: multiple departments, an employee with multiple
   purchases in the same department that should out-total a single larger purchase from
   someone else, and the empty-list case. **Don't construct an exact tie between two
   employees' totals in the same department** — which one wins a tie isn't something this
   collector pipeline guarantees deterministically (it depends on `HashMap` iteration order),
   so a tie-based test would be flaky, not a real bug to chase.
2. Red → fail. Implement `topSpenderPerDepartment()`. Green → pass.

## After you're green — compare against the imperative version

Once your test passes, read `solution/ImperativeApproach.java` — same problem, solved with
nested `HashMap`s and a manual max-tracking loop, no streams at all. Worth sitting with the
side-by-side for a minute: which one would you rather debug at 2am from a stack trace? Which
one is easier to modify if the requirement changes to "top 3 spenders" instead of just the
top 1? Both are legitimate questions with real answers, not just "functional is always
better."

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q055_top_spender_per_department.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
