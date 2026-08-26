# Q006 — Kafka Consumer Groups

**Category:** Concepts / Messaging  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Question

Explain Kafka consumer groups.

Answer all of the following:

1. **What is a consumer group?** What problem does it solve vs a single consumer?
2. **Partition assignment** — how are partitions distributed across consumers in a group? What is the rule about max parallelism?
3. **Rebalance** — what triggers one? What happens during it? Why can it be problematic?
4. **Offset management** — who tracks the offset? What is `auto.commit` and why might you disable it?
5. **At-least-once vs exactly-once** — which does a consumer group give you by default, and why?
6. **Consumer lag** — what is it, how do you monitor it, and what does high lag indicate?

---

## Constraints

- No code required — written explanation only
- Use concrete examples (e.g. "topic with 4 partitions, 3 consumers")
- Name the relevant Kafka internals where applicable (`__consumer_offsets`, Group Coordinator, etc.)

---

## Write your answer in `MyAnswer.md`

---

## Evaluation Rubric

| Area | Strong Answer |
|---|---|
| Consumer group purpose | Horizontal scaling + independent consumption per group |
| Partition rule | Max parallelism = partition count; extra consumers sit idle |
| Rebalance trigger | Consumer join/leave/crash, partition count change |
| Rebalance cost | Stop-the-world pause, duplicate processing risk during rebalance |
| Offset storage | `__consumer_offsets` topic, not Zookeeper (modern Kafka) |
| auto.commit risk | Commits before processing = data loss on crash; disable for at-least-once guarantee |
| Lag | Partition end offset minus consumer offset; monitored via JMX / kafka-consumer-groups.sh |
