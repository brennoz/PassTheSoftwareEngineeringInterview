# Design Google Docs

## What it is
Real-time collaborative document editor. Multiple users edit same doc simultaneously, see each other's cursors, changes merge without conflicts. Hard problems: concurrent edit conflict resolution, low-latency sync, offline support, version history at scale.

---

## Scale Constraints (set these early in interview)
- 1B+ documents
- ~50M DAU
- Avg 5 concurrent editors per active doc (bursts to 50+)
- Sub-100ms perceived latency for local edits
- 99.99% durability

---

## Core Concepts

### 1. Operational Transformation (OT) — the hard one
Two users edit same position simultaneously. Naïve last-write-wins corrupts document.

**Problem:**
```
Doc: "Hello"
User A: insert "!" at pos 5  → "Hello!"
User B: insert " World" at pos 5  → "Hello World"
```
If both applied naively → "Hello! World" or "Hello World!" depending on order.

**OT solution:** transform B's operation against A's before applying.
- A's op arrives first → server applies A → doc = "Hello!"
- B's op: insert at pos 5 → transform: shift by A's insertion length → insert at pos 6 → "Hello! World" ✓

**OT invariant:** `apply(apply(doc, opA), transform(opB, opA)) == apply(apply(doc, opB), transform(opA, opB))`

**Complexity:** OT is correct for 2 users. For N users with N concurrent ops, transformation matrix grows O(N²). Google Docs uses server-authoritative OT — all ops go through one server that serializes and broadcasts transforms. Avoids N² problem.

### 2. CRDTs — alternative to OT
Conflict-free Replicated Data Types. Each character gets a globally unique ID + logical clock. No need for a central server to serialize. Used by: Figma, Notion (partial), Automerge library.

**Tradeoff vs OT:**
| | OT | CRDT |
|---|---|---|
| Conflict resolution | Server serializes | Embedded in data structure |
| Latency | Need server roundtrip for convergence | Can converge P2P |
| Complexity | Simple for 2 users, hard for N | Complex data structure, simple protocol |
| Google Docs uses | ✓ OT | — |

### 3. WebSocket for real-time
HTTP polling → too slow, too much overhead. WebSocket → persistent bidirectional connection. Each doc has a "room" (collab session). Server pushes transformed ops to all clients in room.

### 4. Document storage model
Don't store full document per save — store operations (event sourcing). Reconstruct doc by replaying ops. Snapshot periodically for performance.

```
doc_snapshots: { doc_id, version, content_blob, created_at }
doc_ops:        { doc_id, version, user_id, op_json, timestamp }
```
To load doc at version V: find nearest snapshot ≤ V, replay ops from snapshot→V.

### 5. Presence / Cursors
Separate from document content. Ephemeral — loss OK. Redis pub/sub per doc room. Each client broadcasts cursor position every 100ms. TTL on cursor entries — disappear if client disconnects.

---

## Architecture

```
Client (Browser)
    │  WebSocket
    ▼
┌──────────────────┐
│  API Gateway /   │  Routes: REST → Doc Service
│  WS Gateway      │          WS  → Collab Service
└──────┬───────────┘
       │
  ┌────┴──────────────────────────────┐
  │                                   │
  ▼                                   ▼
┌─────────────────┐         ┌──────────────────────┐
│  Doc Service    │         │  Collab Service       │
│  (REST)         │         │  (WebSocket, OT)      │
│  - create/load  │         │  - op ingestion       │
│  - version API  │         │  - OT transform       │
│  - permissions  │         │  - broadcast to room  │
└────────┬────────┘         └──────────┬────────────┘
         │                             │
         ▼                             ▼
  ┌─────────────┐             ┌───────────────────┐
  │ PostgreSQL  │             │ Redis             │
  │ doc_meta    │             │ - op queue/buffer │
  │ permissions │             │ - cursor presence │
  └─────────────┘             │ - session state   │
                              └───────────────────┘
         │
         ▼
  ┌─────────────┐
  │ Object      │
  │ Storage     │  ← snapshots, binary content
  │ (S3)        │
  └─────────────┘
         │
         ▼
  ┌─────────────┐
  │ Kafka       │  ← async: version history, audit, search index
  └─────────────┘
```

---

## Common Patterns

### Pattern 1: Server-Authoritative OT
```
Client A sends op → Collab Service
Collab Service: serialize op (assign version N+1)
               transform against any concurrent ops
               apply to server doc state
               broadcast transformed op to all clients in room
Clients apply op + advance their version
```
Single source of truth = server. Simplifies OT from NxN to linear.

### Pattern 2: Optimistic Local Apply + Rollback
Client applies op locally immediately (optimistic) for perceived < 100ms latency.
Server may transform it → client receives transformed version → reconcile.
If server rejects (rare) → undo local op + apply server version.

### Pattern 3: Periodic Snapshot
Every 100 ops (or every 5min), write snapshot. Prevents unbounded op log.
```
if (op_count % 100 == 0) {
    snapshot = reconstruct(lastSnapshot, ops[lastSnapshotVersion..currentVersion])
    store(snapshot, currentVersion)
}
```

---

## Interview Angles

1. **"How do you handle concurrent edits?"** → OT vs CRDT, explain server-authoritative OT
2. **"How do you store documents?"** → ops + snapshots (event sourcing), not full doc per save
3. **"How do you scale the collab service?"** → one server per doc session (sticky routing), shard by doc_id; if server fails, clients reconnect, server reconstructs state from DB
4. **"How do you handle offline?"** → client queues ops locally, replays on reconnect; server transforms queued ops against changes that happened while offline
5. **"What's the data model?"** → doc_metadata, doc_ops (op log), doc_snapshots (periodic), permissions
6. **"How do cursors work?"** → Redis pub/sub per room, ephemeral, TTL-based cleanup
7. **"How do you do version history?"** → op log IS the history; replay to any version; show diffs as op annotations per user+timestamp

---

## Key Numbers to Cite
- WebSocket: ~50KB RAM per connection on server → 1M concurrent connections = 50GB RAM → need horizontal scaling
- Op size: ~50-200 bytes per keystroke
- Snapshot threshold: every 100-1000 ops or time-based
- Conflict window: ops within ~200ms window are "concurrent" and need OT

---

## AWS Equivalent (for reference)
| Component | AWS |
|---|---|
| Collab Service WS | API Gateway WebSocket API |
| Op storage | DynamoDB (doc_id + version as sort key) |
| Snapshots | S3 |
| Presence | ElastiCache Redis |
| Op fan-out | AppSync (real-time subscriptions) |
