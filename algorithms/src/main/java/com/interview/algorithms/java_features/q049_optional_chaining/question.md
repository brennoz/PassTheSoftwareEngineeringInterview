# Exercise — Optional Chaining: Email Domain Resolver (Easy)

**Optional methods:** `flatMap`, `filter`, `map`, `orElse`
**Estimated time:** 15 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

Implement, in `MyAnswer.java`:

```java
public static String resolveEmailDomain(Optional<User> maybeUser)
```

Given `User(String id, Optional<String> email)`, return the domain part of the user's email
(everything after `@`) — or `"unknown"` if any of these hold:

- `maybeUser` is empty (no user)
- the user exists but has no email on file
- the email is present but malformed (no `@` in it)

## Hard constraints (this is the actual point of the exercise)

- **No `.get()`** anywhere.
- **No `isPresent()` + `.get()`** pattern.
- **No manual `if (maybeUser.isPresent())` branching at all.** The whole thing should read as
  one Optional pipeline.

If you find yourself writing `if`, stop — there's an Optional method for what you're trying to
do.

## Key questions before coding

- `User.email()` itself returns `Optional<String>`. If you call `.map(User::email)` on
  `maybeUser`, what type do you get back? Why is that a problem, and which method fixes it?
- Where does the `@`-validity check belong in the chain — before or after you've extracted the
  email string? What Optional method expresses "keep going only if this condition holds"?

## TDD workflow

1. Write `MyAnswerTest.java` in `src/test/java/.../q049_optional_chaining/` yourself — no
   test file is provided. Cover the four cases in the "return `unknown`" list above, plus the
   happy path.
2. Red → fail (throws `UnsupportedOperationException`). Implement `resolveEmailDomain()`.
   Green → pass.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q049_optional_chaining.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
