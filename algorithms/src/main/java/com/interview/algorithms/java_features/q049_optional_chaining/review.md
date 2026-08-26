# q049 Review — Optional Chaining: Email Domain Resolver

**Result: PASS — 4/4 tests, all hard constraints met**

---

## What You Got Right

- Correct `flatMap(User::email)`, not `map` — avoided the `Optional<Optional<String>>` trap the
  question specifically flagged. Method reference used idiomatically.
- `filter(email -> email.contains("@"))` placed exactly right — gates before the substring
  extraction, not after, so a malformed email never reaches `indexOf`/`substring`.
- Zero `.get()`, zero `isPresent()+get()`, zero manual `if` — genuinely one pipeline, which was
  the actual point of the exercise, not just "produces the right output."
- `orElse("unknown")` closes the chain correctly for all three failure cases in one place,
  rather than handling them separately.
- Test coverage: happy path + all three `"unknown"` triggers (empty Optional, no email,
  malformed email) — matches the four cases named in the problem statement exactly. Right
  size for an easy-tier exercise (4 tests, no padding).

## Minor Notes (not deductions)

- `indexOf("@")` (String arg) vs `indexOf('@')` (char arg) — both correct, the char overload
  is marginally more idiomatic/avoids an unnecessary single-char String allocation, but this
  is a style nitpick, not a bug.

## Summary

Clean first-attempt pass — correct algorithm, correct constraint adherence, well-chosen tests.
Matches the reference solution's approach exactly (`flatMap` → `filter` → `map` → `orElse`).

---

**Difficulty progression:** 1st consecutive pass at `java` category (easy). One more clean
pass → bump to medium next `next java`.
