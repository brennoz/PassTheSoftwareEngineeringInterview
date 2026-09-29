package com.interview.pairingdrill.q075_transaction_reconciliation;

public interface ExternalAuditService {

    // May throw AuditException (unchecked) if the downstream audit system rejects the entry.
    void logTransaction(Transaction transaction);
}
