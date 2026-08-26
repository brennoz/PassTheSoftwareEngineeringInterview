package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.util.Optional;

// Pattern: Repository (Port in Hexagonal Architecture).
// This interface is the PORT — the contract the domain defines.
// The ADAPTER (InMemoryAccountRepository / JpaAccountRepository) lives in the infrastructure layer.
// AccountService depends only on this interface → Dependency Inversion Principle.
// Result: swapping Postgres for DynamoDB requires a new impl, zero changes to AccountService.
//
// Balance lives here, NOT in Account record:
//   If balance were a field on Account, every deposit/withdraw would require reading
//   the full Account, mutating it, and writing it back — a read-modify-write cycle
//   vulnerable to lost updates under concurrent access.
//   Storing balance separately as a scalar allows atomic increment/decrement operations:
//   In prod (Postgres): UPDATE accounts SET balance = balance + ? WHERE id = ? → atomic at DB level.
//   In prod (Redis cache): INCRBYFLOAT key delta → atomic at Redis level (single-threaded eval).
//   In prod (DynamoDB): UpdateItem with ADD expression → atomic conditional write.
//
// subtractFromBalance must guard against overdraft — the spec requires the check here
//   so the atomicity of "check + subtract" is at the repository boundary, not split across layers.
public interface AccountRepository {

    // Upsert — create or replace. In prod: JPA save() or DynamoDB PutItem.
    void save(Account account);

    Optional<Account> findById(String id);

    // Returns the new balance after addition. Atomic in prod (see above).
    BigDecimal addToBalance(String accountId, BigDecimal amount);

    // Returns the new balance after subtraction.
    // Throws InsufficientFundsException if balance < amount (atomically checked).
    BigDecimal subtractFromBalance(String accountId, BigDecimal amount);

    BigDecimal getBalance(String accountId);
}
