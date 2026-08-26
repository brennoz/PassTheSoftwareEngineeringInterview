---
name: bank-ood-foundation
description: First OOD bank lesson — Account hierarchy, Template Method, BigDecimal, deadlock-safe transfer
metadata:
  type: project
---

# Bank OOD — Foundation

User started bank OOD teaching from Lesson 01.

**Prior context:** passed Parking Lot (q013) — can model basic entities and interfaces.

**Key concepts introduced:**
- `Account` as abstract base with Template Method (`withdraw` + `canWithdraw` hook)
- `Money` as value object using `BigDecimal` + `Currency`
- `Transaction` as immutable record (Java record type)
- Deadlock prevention in `TransferService` via consistent lock-acquisition order

**Why:** User needs to produce a bank class diagram + Java skeleton within 25 min in an interview.

**How to apply:** When next bank OOD question comes up, check if user can reproduce the hierarchy without looking at the lesson.

**Lesson file:** `lessons/0001-bank-domain-model.html`
