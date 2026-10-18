# q081 — Booking Lifecycle

**Lessons:** [0001 Booking Lifecycle State Machine](../../../../../topics/ood/booking-lifecycle/0001-booking-lifecycle-state-machine.html)

**Category:** OOD  
**Difficulty:** Medium  
**Domain:** Rail / Travel  
**Patterns:** State machine (enum with transition guards), BigDecimal, guard exceptions

---

## Scenario

Design a `BookingService` for a rail ticketing platform. A booking moves through
a defined lifecycle: `PENDING → CONFIRMED → TICKETED`, with cancellation possible
from certain states.

You must implement the full state machine with correct transition guards.

---

## Requirements

1. `Booking` entity — holds `id`, `status` (typed enum), `amount` (exact decimal), `passengerId`
2. `BookingStatus` enum — encodes which predecessors are valid for each state
3. `BookingService` — confirm, cancel, issue, calculateRefund operations
4. Invalid transitions must throw `InvalidStateTransitionException` (not silently ignore)
5. Refund = 80% of booking amount, `BigDecimal` arithmetic, 2dp `HALF_UP` rounding

### Valid state transitions

```
PENDING   → CONFIRMED  (confirm)
PENDING   → CANCELLED  (cancel)
CONFIRMED → TICKETED   (issue)
CONFIRMED → CANCELLED  (cancel)
TICKETED  → (terminal — no cancellation once ticket is printed)
```

---

## Constraints

- No Spring annotations — pure OOD
- `BigDecimal` for all monetary values — never `double`
- `BookingStatus` enum owns the transition rules (not a map in BookingService)
- `Booking` keeps mutable setters (JPA-friendly entity, not a record)

---

## Files

- `answer/` — implement here
- `solution/` — reference (peek only after attempt)

Run: `mvn test -pl ood`  
`hint` → nudge · `done` → review
