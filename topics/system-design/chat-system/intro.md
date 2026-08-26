# Chat System — System Design

## What it is
A real-time messaging system where users send and receive messages with low latency (< 100ms). Core challenge: HTTP is request-response (client pulls), but chat needs the server to **push** to clients. Solution: **WebSocket** — a persistent, full-duplex TCP connection. Used in: 1:1 chat, group chat, customer support tools, trading platforms (sport betting in-play alerts), banking notifications.

---

## Core Concepts

### 1. Why WebSocket, not HTTP polling?

| Approach | How | Latency | Server load |
|---|---|---|---|
| Short polling | Client hits `/messages?after=X` every N seconds | High (N second delay) | Many empty requests |
| Long polling | Server holds request open until message arrives | Medium | Fewer requests but many open connections |
| **WebSocket** | Persistent TCP connection, server pushes instantly | **< 100ms** | One connection per user, held open |
| SSE (Server-Sent Events) | One-way push from server | Low | Good for notifications, not bidirectional |

**WebSocket** wins for chat: bidirectional, low overhead (no HTTP headers per message), native browser support.

---

### 2. High-Level Architecture

```
Client A ──WebSocket──► [Chat Service A]──►[Message Queue (Kafka)]──► [Chat Service B]──WebSocket──► Client B
                               │                                              │
                          [Presence]                                    [Push Notification]
                          [Redis pub/sub]                               (for offline users)
                               │
                          [Message Store]
                          (MongoDB / Cassandra)
```

**Key insight:** Client A and Client B may be connected to *different* Chat Service instances. The Chat Service receiving A's message must route it to the service holding B's connection. Two approaches:

| Approach | Mechanism | Tradeoff |
|---|---|---|
| **Redis pub/sub** | Publisher posts to channel; all services subscribed to that channel receive it | Simple, fast, at-most-once delivery |
| **Kafka** | Message written to topic; consumer service reads and delivers | Durable, replayable, at-least-once |

For 1:1 chat at scale → Redis pub/sub (low latency). For group chat or audit trail → Kafka.

---

### 3. Message Storage — Why MongoDB?

Messages have variable structure (text, image, video, reactions) and are append-heavy (inserts, rarely updates). MongoDB fits:

```js
{
  _id: ObjectId,
  conversationId: "conv_123",
  senderId: "user_A",
  content: "Hello",
  type: "TEXT",          // TEXT | IMAGE | VIDEO | SYSTEM
  timestamp: ISODate,
  sequenceId: 1042,      // monotonic per conversation
  deliveryStatus: {
    "user_B": "READ",    // per-recipient delivery tracking
    "user_C": "DELIVERED"
  }
}
```

**Cassandra alternative:** better for very high write throughput (millions msg/sec). Partition key = `(conversationId, bucket)` where bucket = time window. Trades rich query ability for write scalability.

---

### 4. Message Ordering

Two users send simultaneously → both arrive at server → which is "first"?

**Problem:** wall-clock timestamps are unreliable (clock skew between servers).

**Solution: sequence ID per conversation**

```
Option A: DB auto-increment       → bottleneck at high write rate
Option B: Redis INCR per conv     → atomic, fast, O(1)
Option C: Snowflake ID            → globally unique, time-ordered, but not per-conversation
Option D: server-assigned sequence → Chat Service assigns seq on receipt, monotonic per conversation
```

**Standard answer:** Redis `INCR conv:{conversationId}:seq` gives atomic, per-conversation sequence numbers at O(1). Gap in sequence on client = missed message, trigger re-fetch.

---

### 5. Presence (Online / Offline)

```
User connects WebSocket → SET presence:{userId} "ONLINE" EX 30  ← TTL 30s
User heartbeat every 20s → SETEX presence:{userId} "ONLINE" 30  ← refresh TTL
User disconnects → DEL presence:{userId}  (or let TTL expire)

Check: GET presence:{userId}  → "ONLINE" | nil (offline)
```

**Why TTL?** Graceful disconnect fires the DEL. Ungraceful disconnect (network drop, crash) fires nothing — TTL ensures stale presence auto-expires.

**At scale:** Redis is shared across all Chat Service instances. All pods read the same presence. Alternative: dedicated Presence Service with its own Redis cluster.

---

### 6. Push Notifications (Offline Users)

```
Message arrives → check presence:{userId}
    ONLINE  → deliver via WebSocket (in-memory routing)
    OFFLINE → publish to push notification service
                → APNs (iOS) / FCM (Android) / Web Push
```

**AWS:** SNS → routes to APNs / FCM based on platform endpoint. Or: SQS queue consumed by notification worker → calls APNs/FCM directly.

---

### 7. Read Receipts

Three states per message per recipient: **SENT → DELIVERED → READ**

```
SENT:      message written to DB
DELIVERED: recipient's device received it (WebSocket ACK or FCM delivery receipt)
READ:      recipient opened the conversation (client sends explicit READ event)
```

Store in message document or in a separate `MessageStatus` collection keyed by `(messageId, recipientId)`.

**Scaling read receipts:** in group chats with 500 members, one message = 500 status updates. Batch writes, don't update per-member in the hot path.

---

### 8. 1:1 vs Group Chat

| | 1:1 | Group Chat |
|---|---|---|
| Fan-out | 1 recipient | N recipients (fan-out on write) |
| Message routing | Route to one connection | Broadcast to all online members, push to offline |
| Storage | Simple conversation doc | Membership list needed |
| Presence | Check 1 user | Check all N members |
| Read receipts | Simple (2 states) | Per-member, expensive at large N |

**Group chat at scale:** cap group size (e.g. 500 members). Beyond that → broadcast channel pattern (no per-member delivery tracking).

---

## Common Patterns

### Pattern 1: Message deduplication on client
Network retry → client resends message → server must deduplicate.
Each message has client-generated `clientMsgId` (UUID). Server checks: `existsByClientMsgId()` before insert.

### Pattern 2: Message pagination (history load)
```
GET /conversations/{id}/messages?before={sequenceId}&limit=50
```
Load 50 messages before a given sequence → cursor-based pagination. Never offset-based (offset=500 on 10M messages = full scan).

### Pattern 3: WebSocket connection management
```
Client disconnects → reconnects → sends: { lastSeenSeqId: 1041 }
Server replies with all messages after 1041 → gap-fill on reconnect
```

---

## AWS Mapping

| Component | Technology | AWS Equivalent |
|---|---|---|
| WebSocket connections | Chat Service (Spring WebSocket / Netty) | API Gateway WebSocket API |
| Message fan-out | Redis pub/sub | ElastiCache Redis |
| Message durability | Kafka | MSK (Managed Kafka) |
| Message store | MongoDB | DocumentDB or DynamoDB |
| Presence | Redis TTL keys | ElastiCache |
| Push notifications | APNs/FCM gateway | SNS (platform endpoints) |
| Media storage | Object store | S3 + CloudFront |
| Connection state | Stateful per pod | NLB (sticky sessions) or API Gateway WebSocket |

**API Gateway WebSocket:** manages WebSocket connections, routes to Lambda or ECS. No sticky session needed — connection ID stored in DynamoDB, any Lambda can look up and push to connection ID.

---

## Interview Angles

| Question | Answer |
|---|---|
| Why WebSocket not HTTP? | HTTP is request-response; server can't push. WebSocket = persistent full-duplex TCP, sub-100ms delivery |
| How do messages route across servers? | Redis pub/sub per conversation channel; or store connection→server mapping in Redis, route explicitly |
| How ensure message order? | Redis INCR per conversation gives atomic sequence. Client detects gaps, re-fetches |
| How handle offline users? | Check Redis presence → miss → push via APNs/FCM/SNS |
| MongoDB vs Cassandra? | MongoDB: rich queries, flexible schema, good < 10M msg/day. Cassandra: 100M+ msg/day, pure append, time-bucketed partition key |
| Read receipts at scale? | Batch updates; cap group size; separate status collection; async write via Kafka |
| What breaks in multi-AZ? | WebSocket connections are stateful → sticky routing needed (NLB) or connection ID map in Redis |
| How handle reconnect? | Client sends `lastSeenSeqId` on reconnect → server streams missed messages → gap-fill |

---

## Key Numbers
- WebSocket overhead: ~2 bytes/frame vs ~800 bytes HTTP header per poll
- Redis pub/sub latency: < 1ms within same region
- Typical chat app: 50 concurrent connections per server → 10k users per mid-size EC2
- Group chat fan-out threshold: > 500 members → switch to pull model or broadcast channel
- Message retention: 30 days hot (MongoDB/DynamoDB), archive to S3 Glacier after
