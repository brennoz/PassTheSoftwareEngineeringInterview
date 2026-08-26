package com.interview.ood.q036_bank_service_layer.solution;

import java.util.List;
import java.util.Optional;

// Pattern: Repository Port — same Hexagonal principle as AccountRepository.
// TransferService depends on this interface, not on InMemoryTransferRepository.
// In prod: JpaTransferRepository implements this and is injected by Spring.
//
// In prod (audit): Transfer rows are immutable — no UPDATE/DELETE grants on the DB role.
// In prod (idempotency): findById() lets the service check "did this transfer already happen?"
//   before executing. The Transfer id (UUID) is the idempotency key — if the client retries
//   due to a timeout, the service returns the existing Transfer instead of double-transferring.
// In prod (Kafka Outbox): save() also writes to an outbox table in the same @Transactional;
//   Debezium picks up the outbox row and publishes a TransferCompleted event to Kafka.
public interface TransferRepository {

    void save(Transfer transfer);

    Optional<Transfer> findById(String id);

    List<Transfer> findByAccountId(String accountId);
}
