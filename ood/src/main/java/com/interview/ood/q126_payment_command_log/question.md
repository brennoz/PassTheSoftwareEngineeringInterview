**Lessons:** [0001 Command Pattern](../../../../../topics/ood/command/0001-command-pattern.html)

# q126 — Payment Transaction Log

**Category:** OOD | **Difficulty:** Medium | **Ideal time:** 25 min  
**Domain:** Payment / Fintech  
**Patterns:** Command (execute / undo / audit log), BigDecimal, ArrayDeque as history stack

---

## Scenario

A digital wallet service needs to support payment operations with two additional capabilities:
**undo** (reverse the last operation — compensation transaction) and an **audit log** (immutable
record of everything that happened, including undos).

---

## Requirements

1. `PaymentCommand` interface — `execute()`, `undo()`, `description()` (returns a short label for the audit log)
2. `Wallet` — receiver; holds `BigDecimal` balance; exposes `debit(amount)` and `credit(amount)`
3. `DebitCommand` and `CreditCommand` — concrete commands; each holds a `Wallet` reference and `BigDecimal` amount
4. `PaymentProcessor` — invoker; `execute(PaymentCommand)`, `undoLast()`, `getAuditLog()`

### Behaviour

- `execute(cmd)` — calls `cmd.execute()`, pushes onto history stack, appends `"EXECUTED: {description}"` to audit log
- `undoLast()` — pops last command, calls `cmd.undo()`, appends `"UNDONE: {description}"` to audit log
- `undoLast()` when history is empty — throw `IllegalStateException("Nothing to undo")`
- `getAuditLog()` — returns unmodifiable list

### BigDecimal rules

- Constructor: `new BigDecimal("10.00")` — never `new BigDecimal(0.1)` (floating-point imprecision)
- Comparison: `compareTo`, never `equals` (scale-sensitive)
- Arithmetic: `add`, `subtract` only — no division needed here

---

## Files

- `answer/` — implement here
- `solution/` — reference (peek only after attempt)

Run: `mvn test -pl ood`  
`hint` → nudge · `done` → review
