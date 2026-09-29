# idempotency — Lesson Index

| File | Concepts covered |
|---|---|
| [0001-idempotency-fundamentals.html](0001-idempotency-fundamentals.html) | Definition, HTTP method idempotency (GET/PUT/DELETE vs POST/PATCH), idempotency key pattern, server-side algorithm (reserve→process→complete in one TX), DB UNIQUE constraint, INSERT ON CONFLICT, optimistic lock (@Version), Kafka idempotent producer + consumer deduplication, Redis SET NX, pitfalls (shared mutable state across pods, no TTL, request hash mismatch), AWS: DynamoDB conditional write, SQS FIFO MessageDeduplicationId, Lambda Powertools Idempotency, Step Functions execution names |
