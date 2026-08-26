# Q006 — My Answer: Kafka Consumer Groups

Write your answer below. Cover all 6 areas from question.md.

1. A Consumer Group is a group of consumers. Only a consumer per consumer group can consume from a partition.
It solves the parallel processing, fan-out problem.

2. I think partitions are assigned via hash(key) % partitionNumber, key couls be customerId for instance

5. At-least once is the default. Events should be created idempotent so that you are not forced to use exactly-once settings which are expensive.

6. High Lag indicates that consumers are not consuming events for a long time. It indicates that there is an interruption in the workflow.

---

