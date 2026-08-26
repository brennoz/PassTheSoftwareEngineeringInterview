# Q005 — Design Google Docs

**Category:** System Design  
**Difficulty:** Medium  
**Ideal Time:** 30 min

---

## Problem

Design a real-time collaborative document editing system like Google Docs.

Multiple users can edit the same document simultaneously. Changes from one user must appear on all other users' screens in near real-time, and the final document must be consistent for everyone.

---

## Requirements to Cover

### Functional
- Create, read, update, delete documents
- Multiple users edit same document concurrently (up to 50 simultaneous editors)
- Real-time propagation of changes to all connected clients
- Show other users' cursors / presence
- Full version history — view any past version
- Offline editing — sync when reconnected

### Non-Functional
- Sub-100ms perceived latency for local edits
- 99.99% document durability
- Support 1B documents, 50M DAU
- Eventual consistency across all clients (convergence guaranteed)

---

## What to Produce

Write your answer in `MyAnswer.md`. Cover:

1. **Conflict resolution** — how do concurrent edits from two users get merged without corruption?
2. **Core components** — what services exist, what does each own?
3. **Data model** — tables/schemas for documents, operations, permissions
4. **Real-time transport** — protocol choice + justification
5. **Presence / cursors** — how are live cursors shown?
6. **Version history** — how do you reconstruct a document at any past version?
7. **Offline support** — what happens when a user goes offline and comes back?
8. **Scale bottleneck** — where does the system break at 10x load, and how do you fix it?

---

## Constraints to State Upfront

You should explicitly state assumptions on:
- Write throughput (ops/sec per active doc)
- Consistency model (strong vs eventual)
- Whether you support rich formatting, images, comments

---

## Evaluation Rubric

| Area | Strong Answer |
|---|---|
| Conflict resolution | Names OT or CRDT, explains why server-authoritative OT, not just "last write wins" |
| Storage | Op log + snapshots (not full doc per save) |
| Transport | WebSocket, explains why not HTTP polling |
| Presence | Ephemeral store (Redis), separate from doc content |
| History | Replay ops from nearest snapshot, O(ops since snapshot) |
| Offline | Client op queue, server OT on reconnect |
| Scale | Sticky routing by doc_id, snapshot to bound log, Redis cluster for presence |
