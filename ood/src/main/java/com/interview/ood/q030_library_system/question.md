**Lessons:** [0001 Library System Domain Model](../../../../../topics/ood/library-system/0001-library-domain-model.html)

# q030 — Library Management System

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 25 min

---

## Problem

Design a simplified Library Management System. A library has books (each book can have multiple physical copies). Members can borrow, reserve, and return copies. Overdue copies generate fines.

---

## Requirements

1. A `Book` represents a catalogue entry (title, author, ISBN). A library can have multiple physical copies (`BookCopy`) of the same book.
2. A `BookCopy` has a status: `AVAILABLE`, `RESERVED`, `BORROWED`, `OVERDUE`, or `LOST`.
3. Status transitions follow strict rules — use **State Pattern** to model them.
4. A `Member` can borrow at most **3 copies** at a time.
5. A `Loan` is created when a member borrows a copy. It records the due date.
6. When a copy is returned, calculate the fine using a **Strategy Pattern** — two concrete strategies must exist: `DailyFineStrategy` (e.g. £0.50/day) and `FlatFineStrategy` (e.g. £5.00 flat if overdue).
7. `LibraryService` orchestrates checkout and return operations.

---

## Class diagram (skeleton guide)

```
CopyState (interface)
  AvailableState
  ReservedState
  BorrowedState
  OverdueState
  LostState

FineCalculationStrategy (interface)
  DailyFineStrategy
  FlatFineStrategy

Book
BookCopy  → holds CopyState
Member    → List<Loan> activeLoans
Loan      → Member + BookCopy + dueDate
Fine      → Loan + BigDecimal amount
LibraryService → FineCalculationStrategy
```

---

## Constraints

- Use `BigDecimal` for all monetary amounts — never `double` or `float`
- `BookCopy` state object must be the only place that throws `IllegalStateException` on invalid transitions
- `LibraryService.checkout()` must check borrow limit **before** changing BookCopy state
- No Spring, no persistence, pure Java 21

---

## Folder layout

```
q030_library_system/
├── question.md
├── solution/        ← full reference, split into one class per file, TDD-order comments,
│                       LibraryMain runnable demo (both src/main and src/test)
└── answer/
    └── attempt1/     ← historical: first graded attempt, 2/4 tests initially (State pattern
                         miswired + BigDecimal `==` bug, both documented in solution/'s
                         comments), later fixed in place to 4/4. Left untouched as a record.

    A fresh answer/attempt2/ (or similar) is the place to retry this from scratch.
```

---

## Hints

If stuck → type `hint`
To see full solution → type `solution`
To submit → type `done`
