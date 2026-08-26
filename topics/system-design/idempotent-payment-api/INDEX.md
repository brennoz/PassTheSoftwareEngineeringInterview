# idempotent-payment-api — Lesson Index

| File | Concepts covered |
|---|---|
| [0001-idempotent-payment-api.html](0001-idempotent-payment-api.html) | Idempotency key (client-generated UUID, Idempotency-Key header), server-side algorithm (lookup → INSERT → process → COMPLETED), DB unique constraint vs SELECT FOR UPDATE for concurrent duplicates, request hash (SHA-256, 422 on mismatch), Outbox Pattern (atomic payment + event in one TX, relay publishes to Kafka), TTL on idempotency records, AWS: DynamoDB conditional write (attribute_not_exists), SQS FIFO MessageDeduplicationId, DynamoDB Streams + Lambda relay |
