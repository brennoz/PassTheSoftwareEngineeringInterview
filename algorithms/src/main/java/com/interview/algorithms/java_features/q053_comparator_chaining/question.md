# Exercise — Comparator Chaining: Multi-Field Sort (Medium)

**Concepts:** `Comparator.comparing`, `thenComparing`, `reversed`, per-field vs whole-chain
reversal
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

Given `Employee(String name, String department, int salary)`, implement, in `MyAnswer.java`:

```java
public static List<Employee> sortByDepartmentThenSalaryDesc(List<Employee> employees)
```

Sort rule: **department ascending** (A→Z), then **within each department, salary
descending** (highest first). Return a new sorted list — do not assume the input list is
mutable, and do not mutate it.

## Hard constraints (this is the actual point of the exercise)

- Build the ordering by **chaining `Comparator` methods** (`comparing`, `thenComparing`,
  `reversed`) — no hand-written `compare(Employee a, Employee b)` method with `if`/`else`
  branches comparing fields manually.
- **The subtlety to get right:** only the SALARY comparison is descending. Department stays
  ascending. If you build the whole chain first and then call `.reversed()` on the combined
  result, you'll flip BOTH fields — department would come out Z→A too, which is wrong. Figure
  out where in the chain `reversed()` actually needs to go.

## Key questions before coding

- `Comparator.comparing(Employee::department)` gives you department-ascending. How do you
  attach a SECOND ordering that only applies when two employees have the same department?
- Where exactly does `.reversed()` belong so it flips only the salary comparison, not the
  whole multi-field chain?

## TDD workflow

1. Write `MyAnswerTest.java` yourself in `src/test/java/.../q053_comparator_chaining/`. Use
   `List.of(...)` for your input (immutable) — if your implementation tries to sort the input
   list in place rather than sorting a copy, this will throw
   `UnsupportedOperationException`, which is itself a useful signal your approach needs to
   change. Cover: multiple departments correctly ordered, and within one department, salary
   correctly descending (include a case that would look "sorted" if you accidentally reversed
   department too, so the test would actually catch that specific bug).
2. Red → fail. Implement `sortByDepartmentThenSalaryDesc()`. Green → pass.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q053_comparator_chaining.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
