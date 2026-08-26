# Exercise — Preferred Contact Channel (Medium)

**Concepts:** `Optional.or(Supplier<? extends Optional<? extends T>>)` — lazy fallback between
two `Optional`s
**Estimated time:** 15 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Given a customer's mobile number (may be `null`) and a way to look up their email address (an
expensive operation — imagine a service call), resolve their preferred contact channel.
Implement, in `MyAnswer.java`:

```java
public static Optional<String> resolvePreferredContact(String mobileNumber, Supplier<Optional<String>> emailLookup)
```

Prefer the mobile number if present. If it's absent, fall back to `emailLookup.get()`. If
neither yields a value, return `Optional.empty()`.

## Hard constraints (the actual point of the exercise)

- Use `Optional.or(...)` — not `orElse`/`orElseGet` (both of those unwrap to a plain value,
  which doesn't fit here since the fallback itself might not find anything either).
- `emailLookup` must **never be invoked** when `mobileNumber` is present. This isn't just about
  ignoring its result — the lookup must not run at all.

## Key questions before coding

- `Optional` has `orElse(T)`, `orElseGet(Supplier<? extends T>)`, and `or(Supplier<? extends Optional<? extends T>>)`.
  What's different about what `or()` returns compared to the other two, and why does that
  difference matter when the fallback source can itself be empty?
- If you wrote `Optional.ofNullable(mobileNumber).or(emailLookup.get())` instead of
  `.or(emailLookup)`, would it still compile? Would it still be lazy?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: mobile present (email lookup never runs — prove
   this with a flag/counter, not just by checking the returned value); mobile absent, email
   found; neither present.
2. Red → fail. Implement `resolvePreferredContact()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — an explicit `if (mobileNumber != null)` guard before
the lookup runs, instead of `or()` making that ordering the only option available.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q066_preferred_contact.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
