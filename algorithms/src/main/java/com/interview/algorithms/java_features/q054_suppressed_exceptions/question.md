# Exercise — try-with-resources: Close Order + Suppressed Exceptions (Medium)

**Concepts:** try-with-resources, LIFO close order, `Throwable.getSuppressed()`, primary vs
suppressed exceptions
**Estimated time:** 25 min
**Your file:** `MyAnswer.java` (this directory)
**Given:** `LoggingResource.java` (already implements `AutoCloseable` — don't modify it)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

`LoggingResource(String name, boolean throwOnClose)` throws a `RuntimeException("close
failed: " + name)` from `close()` if `throwOnClose` is `true`, otherwise closes silently.

Implement, in `MyAnswer.java`:

```java
public static List<String> describeFailure(boolean resourceAThrowsOnClose, boolean resourceBThrowsOnClose)
```

- Open a `LoggingResource("A", resourceAThrowsOnClose)` and a
  `LoggingResource("B", resourceBThrowsOnClose)`, in that order, using **try-with-resources**.
- Inside the `try` block, unconditionally throw `new RuntimeException("body failed")` — the
  body always fails, regardless of the parameters.
- Catch the resulting exception and return a `List<String>`:
  - First entry: `"primary: " + <the caught exception's message>`
  - Then one `"suppressed: " + <message>"` entry for each exception `close()` actually threw,
    in whatever order `Throwable.getSuppressed()` returns them.

## Hard constraints (this is the actual point of the exercise)

- **Use try-with-resources** (`try (LoggingResource a = ...; LoggingResource b = ...) { }`) —
  not a manual `try`/`finally` calling `.close()` yourself. The whole point is letting the JVM
  handle the close sequence and the suppressed-exception bookkeeping automatically; doing it
  by hand and trying to replicate the same behavior defeats what the feature is for.
- Do not swallow or discard `getSuppressed()` — every close failure that actually happened
  must show up in the result, in the order the JVM attaches them.

## Key questions before coding — work these out BEFORE writing code

- Resources in a try-with-resources statement close in what order relative to how they were
  declared? (Not assumed — reason about why that order makes sense: think about resource
  dependencies, e.g. a buffered stream wrapping a file stream — which one needs to close
  first?)
- The `try` block's body throws. Then `close()` is called on each resource in turn, and ALSO
  throws. What happens to the body's original exception — does the close() exception replace
  it, or does something else happen? What Java 7+ API lets you find out?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: neither resource throws on close (result has
   only the primary entry), only one throws, and both throw (this last case is where the
   ORDER assertion actually matters — work out from your answer to the close-order question
   above which resource's close-failure should appear first in the suppressed list).
2. Red → fail. Implement `describeFailure()`. Green → pass.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q054_suppressed_exceptions.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
