package com.interview.ood.q036_bank_service_layer.solution;

// Pattern: Value Object (via enum) — encodes double-entry bookkeeping direction.
// Every DEBIT on one account must have a matching CREDIT on another (conservation of money).
// In prod: a TransferService ensures both sides are written atomically in the same @Transactional.
// In Kafka: each TransactionRecorded event carries this type so downstream consumers
//   (fraud detection, reporting, notification) can filter without loading the full ledger.
public enum TransactionType {
    CREDIT,  // money in  — balance increases
    DEBIT    // money out — balance decreases
}
