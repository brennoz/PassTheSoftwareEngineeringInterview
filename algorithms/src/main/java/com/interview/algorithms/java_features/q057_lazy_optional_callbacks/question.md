# Exercise — Lazy Optional Callbacks: Supplier + Consumer (Medium)

**Concepts:** `Optional.orElseGet(Supplier)` vs `orElse(T)`, `Optional.ifPresentOrElse(Consumer, Runnable)`,
eager vs lazy evaluation, imperative vs functional trade-offs
**Estimated time:** 25 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Given `Customer(String id, String email)`, implement two methods in `MyAnswer.java`:

```java
public static String greet(Optional<Customer> maybeCustomer, Supplier<String> fallbackGreetingSupplier)
```
Returns `"Welcome back, " + email` if a customer is present. Otherwise returns whatever
`fallbackGreetingSupplier.get()` produces.

```java
public static void notifyCustomer(Optional<Customer> maybeCustomer, Consumer<Customer> sendNotification, Runnable logNoCustomerFound)
```
If a customer is present, calls `sendNotification.accept(customer)`. Otherwise calls
`logNoCustomerFound.run()`.

## The actual trap this exercise is testing for

`Optional` has TWO methods that look almost identical: `orElse(T value)` and
`orElseGet(Supplier<? extends T> supplier)`. It's extremely common to reach for `orElse(...)`
out of habit — it reads slightly shorter — and pass it something like
`fallbackGreetingSupplier.get()` directly. That compiles, and passes a test where the
`Optional` is empty. It's still wrong.

## Key questions before coding

- Java evaluates method arguments eagerly, left to right, before the method body runs. If you
  write `optional.orElse(fallbackGreetingSupplier.get())`, when does
  `fallbackGreetingSupplier.get()` actually get called — before or after `orElse` checks
  whether the `Optional` is present?
- Given that, what would go wrong if `fallbackGreetingSupplier` were something expensive (a
  network call, a slow computation) or had an observable side effect (incrementing a counter,
  writing a log line) — and the `Optional` passed in was NOT empty?
- `ifPresentOrElse` takes a `Consumer` for the present case and a `Runnable` for the absent
  case — why a `Runnable` and not another `Consumer` for the second argument? What's different
  about what happens in the "absent" branch that makes `Consumer<Customer>` the wrong shape
  for it?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. For `greet()`, the test that actually matters: pass a
   `Supplier<String>` that sets a boolean flag (or increments a counter) when `.get()` is
   called, alongside a PRESENT `Optional<Customer>`, and assert the flag/counter shows the
   supplier was **never invoked**. A version using `orElse(supplier.get())` would fail this
   specific test even though it might pass a simpler "does it return the right string" test.
2. Red → fail. Implement both methods. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` once your tests pass. Worth noticing: plain `if`/`else`
branching gets you laziness for free — the `else` branch's code simply doesn't run unless
you're in that branch. `orElseGet`/`ifPresentOrElse` exist specifically to preserve that same
"only compute what's needed" property inside a fluent, single-expression style, where Java's
eager argument evaluation would otherwise quietly break it.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q057_lazy_optional_callbacks.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
