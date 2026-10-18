# q111 — Finite State Machine Pattern Matching

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Using a Finite State Machine, determine whether a string contains a substring matching the pattern `@digits#` — i.e., an `@` sign followed by one or more decimal digits, ending with `#`.

Print whether the string **matches** or **does not match**.

---

## States

```
State 0 → initial / reset
State 1 → saw @
State 2 → saw @ followed by one or more digits
State 3 → MATCH (saw @digits#)
```

- If an unexpected character is seen, reset to state 0 (or 1 if it's `@`).

---

## Examples

```
"A1@312#"          → MATCHES
"A1@31279879#"     → MATCHES
"A1312#"           → DOES NOT match (no @)
"A1@31279@r879#"   → DOES NOT match (letter after @)
```

---

## Your task

Edit `MyAnswer.java` — implement `match(String s)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
