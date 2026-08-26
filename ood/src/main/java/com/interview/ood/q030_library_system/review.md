# q030 Library System — Review

**Result: PARTIAL PASS — 2/4 tests fail. Two bugs, same root cause.**

---

## Test Results

| Test | Result |
|------|--------|
| `checkout_changes_copy_status_to_borrowed` | ✅ PASS |
| `checkout_throws_when_borrow_limit_reached` | ✅ PASS |
| `invalid_state_transition_throws` | ❌ FAIL — nothing thrown |
| `return_on_time_produces_no_fine` | ❌ FAIL — expected null, got Fine |

---

## Bug 1 — State Pattern not wired (CRITICAL)

**Category: OO Design — pattern not correctly applied**

### What you wrote

```java
// BookCopy:
public void reserve()    { state = new ReservedState(); }   // direct mutation
public void checkout()   { state = new BorrowedState(); }
public void returnCopy() { state = new AvailableState(); }
public void markLost()   { state = new LostState(); }

// AvailableState:
@Override public void reserve(BookCopy c) { c.reserve(); }   // calls back to BookCopy
```

### What breaks

`BookCopy.reserve()` **directly overwrites `state`** — it never calls `state.reserve(this)`.
So `BorrowedState.reserve()` (which throws) is **never reached**.

A `LOST` copy can be reserved. A `BORROWED` copy can be reserved. Every transition fires regardless of current state. The entire point of State Pattern — guarded transitions — is dead code.

### The invariant

The delegation must go **one way only**:
```
External caller
  → BookCopy.reserve()          [delegate only — no logic here]
    → state.reserve(this)       [current state decides if valid]
      → c.setState(new X())     [state sets the next state]
```

### Fix

```java
// BookCopy — pure delegation, no logic:
public void reserve()    { state.reserve(this); }
public void checkout()   { state.checkout(this); }
public void returnCopy() { state.returnCopy(this); }
public void markLost()   { state.markLost(this); }

// AvailableState — state controls transition:
@Override public void reserve(BookCopy c)    { c.setState(new ReservedState()); }
@Override public void checkout(BookCopy c)   { c.setState(new BorrowedState()); }

// BorrowedState — guard fires:
@Override public void reserve(BookCopy c)    { throw new IllegalStateException("Already borrowed"); }
```

---

## Bug 2 — BigDecimal compared with `==` (CRITICAL)

**Category: Java — reference equality on value type**

### What you wrote (line 280)

```java
return (fine == BigDecimal.ZERO) ? new Fine(loan, BigDecimal.ZERO) : new Fine(loan, fine);
```

**Two problems here:**

**a) `==` on BigDecimal** — compares object references, not numeric values.  
Works by accident when the strategy returns the exact static `BigDecimal.ZERO` constant.  
Breaks the moment any strategy returns `new BigDecimal("0")` or `BigDecimal.valueOf(0)` — those are different objects.

**b) Logic is inverted** — even if the comparison were correct:
- When fine IS zero → you return `new Fine(loan, BigDecimal.ZERO)` — wrong, should return `null`
- When fine is NOT zero → you return `new Fine(loan, fine)` — correct

So the method always returns a Fine object, never null, even for on-time returns.

### Fix

```java
BigDecimal fine = fineStrategy.calculate(loan);
return fine.compareTo(BigDecimal.ZERO) > 0 ? new Fine(loan, fine) : null;
```

**Rule:** Never use `==` or `!=` with `BigDecimal`. Always `.compareTo()` or `.equals()`.
For zero check specifically: `.compareTo(BigDecimal.ZERO) == 0` or `.signum() == 0`.

---

## What was correct

| Area | Assessment |
|------|-----------|
| `DailyFineStrategy.calculate()` | ✅ Correct — `ChronoUnit.DAYS.between`, returns ZERO if not overdue |
| `FlatFineStrategy.calculate()` | ✅ Correct — same pattern |
| `LibraryService.checkout()` | ✅ Correct — limit check before state change, Loan created + added |
| `LibraryService.returnBook()` — structure | ✅ Correct — `setReturnedOn`, `returnCopy()`, `removeLoan()` in right order |
| Borrow limit enforcement | ✅ Correct — guard fires before `copy.checkout()` |
| `BigDecimal` for monetary values | ✅ Used throughout (except the `==` comparison) |
| State names | ✅ All five states named correctly |
| Terminal `LostState` | ✅ All four transitions throw |
| `BorrowLimitExceededException` | ✅ Thrown correctly |

---

## What to fix to get 4/4

1. In `BookCopy`: change all four methods to `state.X(this)` — remove direct state mutation
2. In each concrete state's valid-transition methods: call `c.setState(new Y())` — not `c.X()`
3. In `returnBook()`: replace `fine == BigDecimal.ZERO` with `fine.compareTo(BigDecimal.ZERO) > 0`, fix the ternary so null is returned when no fine

---

## Interview signal

The BigDecimal `==` bug is a **top-3 Java money bug** interviewers deliberately plant in code review questions.
The State Pattern wiring error is the most common mistake in this exact question — "I defined the states but never delegated to them."

Both bugs come from the same mental model gap: **the state object must control the transition, not the entity**. Once that clicks, both fixes are obvious.
