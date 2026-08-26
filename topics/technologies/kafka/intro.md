# Kafka

## What it is
Kafka is a distributed, durable, ordered event log. Producers write events to topics; consumers read from topics at their own pace. Unlike a traditional message queue (RabbitMQ), events are not deleted on consumption — they stay on disk for a configurable retention window. This makes Kafka perfect for: event sourcing, audit logs, replay, fan-out to multiple consumer groups, and decoupling microservices.

---

## Core Concepts

### Topic
Named stream of events. Split into **partitions** for parallelism. A topic is just a label — the data lives in partitions.

### Partition
Ordered, immutable log. Events appended at the end. Each event gets an **offset** (monotonically increasing integer). Ordering guaranteed WITHIN a partition — NOT across partitions.

```
Partition 0:  [0] [1] [2] [3] [4] ...
Partition 1:  [0] [1] [2] ...
Partition 2:  [0] [1] [2] [3] ...
```

Partition count = max parallelism for consumers in the same group.

### Producer
Writes to a topic. Decides which partition via:
- **Key hash** (default): `hash(key) % numPartitions` → same key always → same partition → ordering per key
- **Round-robin**: no key → spread load evenly
- **Custom partitioner**

### Consumer & Consumer Group
- **Consumer:** reads events from partitions, tracks its own offset
- **Consumer group:** a group of consumers sharing the work. Each partition assigned to exactly ONE consumer in the group. Multiple groups can read the same topic independently.

```
Topic: orders (4 partitions)
Group A (3 consumers):
  Consumer 1 → P0, P1
  Consumer 2 → P2
  Consumer 3 → P3

Group B (1 consumer — analytics):
  Consumer 1 → P0, P1, P2, P3  (reads same events, independently)
```

Rule: consumers > partitions → extra consumers sit idle.

### Offset
Position in a partition. Consumer commits offset after processing. Stored in `__consumer_offsets` internal topic (not Zookeeper since Kafka 2.x).

```
Partition 0:  [0] [1] [2] [3] [4]
                              ^
                         consumer offset = 4 (next to read = 4)
```

### Broker
Kafka server. Stores partition data on disk. Cluster = multiple brokers. Each partition has one **leader broker** (handles reads/writes) + N **replica brokers** (ISR = in-sync replicas).

### Replication
`replication.factor=3` → 3 copies of each partition across 3 brokers. Producer `acks` setting controls durability:
- `acks=0`: fire and forget — fastest, data loss possible
- `acks=1`: leader wrote — fast, loss if leader crashes before replication
- `acks=all` (or `-1`): all ISR replicas wrote — slowest, no loss

---

## Delivery Guarantees

| Mode | How | Risk |
|---|---|---|
| At most once | Commit offset BEFORE processing | Loss: crash after commit, before process |
| At-least-once | Commit offset AFTER processing | Duplicate: crash after process, before commit → reprocess |
| Exactly once (EOS) | Idempotent producer + transactional API | No loss, no duplicate — complex, higher latency |

**Default = at-least-once.** Design consumers to be **idempotent** (safe to process same event twice) rather than fighting for exactly-once.

---

## Consumer Rebalance

Triggered when:
- Consumer joins group
- Consumer leaves / crashes (missed heartbeat)
- Partition count changes

During rebalance: **all consumers in the group stop consuming** (stop-the-world). Partitions reassigned. Risk: in-flight offsets not committed → duplicate processing after rebalance.

**Mitigation:** `cooperative-sticky` assignor (incremental rebalance — only moves partitions that must move, others keep consuming).

---

## Consumer Lag

`lag = partition_end_offset - consumer_current_offset`

High lag → consumers can't keep up with producers. Monitor via:
- `kafka-consumer-groups.sh --describe`
- JMX metric `records-lag-max`
- Prometheus + Grafana (kafka_consumer_group_lag)

Fix: add more consumers (up to partition count), or increase partition count + rebalance.

---

## Retention

Events not deleted on consumption. Deleted by:
- **Time:** `retention.ms` (default 7 days)
- **Size:** `retention.bytes` per partition
- **Compaction:** `cleanup.policy=compact` — keeps only the LATEST event per key. Good for changelog topics (current state, not full history).

---

## Key Patterns

### Pattern 1: Fan-out to multiple consumers
```
OrderPlaced event → topic: orders
  Group: inventory-service  → reserve stock
  Group: email-service      → send confirmation
  Group: analytics-service  → update dashboard
All read same events independently. Decoupled.
```

### Pattern 2: Ordering per entity
```java
producer.send(new ProducerRecord<>("payments", customerId, paymentEvent));
// Same customerId → same partition → ordered processing per customer
```

### Pattern 3: Outbox Pattern with Kafka
```
1. Write domain event to outbox table IN SAME DB TRANSACTION as aggregate
2. Outbox poller (Debezium CDC or polling thread) reads unprocessed rows
3. Publish to Kafka
4. Mark as published
→ Guarantees event not lost even if Kafka is temporarily down
```

### Pattern 4: Saga Choreography
```
OrderService     → publishes OrderCreated
PaymentService   → consumes OrderCreated → publishes PaymentProcessed
InventoryService → consumes PaymentProcessed → publishes StockReserved
OrderService     → consumes StockReserved → marks order CONFIRMED
Each step is a consumer + producer. No central orchestrator.
```

---

## Kafka vs RabbitMQ

| | Kafka | RabbitMQ |
|---|---|---|
| Model | Log (events stay) | Queue (events deleted on consume) |
| Ordering | Per partition | Per queue (single consumer) |
| Replay | Yes — seek to any offset | No |
| Fan-out | Multiple consumer groups trivially | Exchange + multiple queues, manual setup |
| Throughput | Millions/sec | Hundreds of thousands/sec |
| Use when | Event sourcing, audit, replay, high throughput | Task queues, RPC, routing, low-latency |

---

## Interview Angles

| Interviewer asks | You say |
|---|---|
| How guarantee ordering? | Key-based partitioning → same key → same partition → ordered |
| How scale consumers? | Add consumers up to partition count; beyond that, increase partitions first |
| What is rebalance? | Partition reassignment when group membership changes; stop-the-world; use cooperative-sticky to minimise pause |
| Exactly-once? | Idempotent producer + consumer transactions; simpler to design idempotent consumers for at-least-once |
| Kafka vs RabbitMQ? | Kafka = replayable log, fan-out, high throughput; Rabbit = task queue, routing, lower latency |
| Consumer lag? | end_offset minus consumer_offset per partition; high lag = consumers too slow; fix: more consumers or more partitions |
| How avoid data loss? | `acks=all`, `min.insync.replicas=2`, `replication.factor=3` |

---

## Key Numbers
- Default retention: 7 days
- Default partition count: 1 (always set higher in prod)
- Recommended replication factor: 3
- Max consumers with ordering guarantee: = partition count
- Heartbeat interval: 3s default; `session.timeout.ms`: 10–30s
