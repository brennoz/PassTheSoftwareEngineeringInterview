# Q028 — Design a Comment System

**Category:** System Design  
**Difficulty:** Medium  
**Ideal time:** 25 min  
**Lesson:** [0001 Comment System Core](../../topics/system-design/comment-system/0001-comment-system-core.html)

---

## Problem

Design a scalable comment system similar to YouTube or Reddit comments.

---

## Functional Requirements

- Users can post a comment on any resource (video, article, post)
- Users can reply to a comment (support at least one level of nesting)
- Users can like or dislike a comment
- Users can fetch comments for a resource with pagination (chronological + top-voted views)
- Authors receive a notification when their comment gets a reply
- Comments can be soft-deleted (hidden but retained for audit)

---

## Non-Functional Requirements

- **Scale:** 100M daily active users; 10M comments written/day; 1B comment reads/day
- **Read:write ratio:** ~100:1 — optimise for reads
- **Latency:** comment fetch < 100ms p99; like < 50ms p99
- **Availability:** 99.9% — comments are not mission-critical (eventual consistency acceptable for likes)
- **Data retention:** comments kept indefinitely (soft delete only)

---

## Out of Scope

- Real-time live updates (WebSocket)
- Full-text search across comments
- Markdown rendering (client concern)
- Comment editing history

---

## What to cover in MyAnswer.md

### 1. Scope confirmation
State what you are and are not designing.

### 2. Data model
- Schema for `comments` table (including hierarchical structure)
- How nested replies are stored — name the pattern and justify
- Schema for `likes` (separate table or counter?)
- Indexes — which columns and why

### 3. API design
- `POST /comments` — create comment or reply
- `GET /comments?postId={id}&cursor={cursor}&limit={n}` — paginated fetch
- `POST /comments/{id}/like` — like a comment
- State request/response shape for each

### 4. Read path
- Pagination strategy — why cursor over offset
- Caching — what to cache, key structure, TTL, invalidation strategy
- Cache stampede — name it and state your mitigation

### 5. Write path
- Like counter — why Redis INCR, how async flush works
- Reply notification — how Comment Service publishes event, what consumes it
- Outbox Pattern — where and why

### 6. Component diagram
Draw boxes and arrows showing: Client → API Gateway → Comment Service → PostgreSQL / Redis / Kafka → Notification Service

### 7. AWS / Distributed environment
Map every component to an AWS service. State one distributed failure mode and how you handle it.

---

## Write your answer in MyAnswer.md
