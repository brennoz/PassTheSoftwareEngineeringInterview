package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TransactionReconciliationService {

    private final RateProvider rateProvider;
    private final ExternalAuditService externalAuditService;

    public TransactionReconciliationService(RateProvider rateProvider, ExternalAuditService externalAuditService) {
        this.rateProvider = rateProvider;
        this.externalAuditService = externalAuditService;
    }

    public Map<String, BigDecimal> reconcileByCurrency(List<Transaction> transactions) {
        // Why Collectors.groupingBy + reducing, not the seeded parallelStream().forEach()
        // mutating a shared HashMap: a Collector's accumulation is owned entirely by the
        // stream pipeline — nothing outside it is mutated concurrently. The seeded version had
        // every thread in the parallel stream calling HashMap.merge() on the SAME map
        // instance; HashMap isn't thread-safe for concurrent structural modification, so
        // entries could be silently lost under load — the "intermittent" symptom the ticket
        // described. This version stays correct even if made parallel later, because
        // groupingBy's merging is handled by the Collector itself, not by the caller's own
        // mutable state.
        Map<String, BigDecimal> totalsByCurrency = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCurrency,
                        Collectors.reducing(BigDecimal.ZERO, this::convertedAmount, BigDecimal::add)));

        // Why the futures are collected and joined here, not fired without being watched: see
        // auditAsync's comment below — this is the other half of that fix.
        List<CompletableFuture<Void>> auditFutures = transactions.stream()
                .map(this::auditAsync)
                .toList();
        CompletableFuture.allOf(auditFutures.toArray(new CompletableFuture[0])).join();

        return totalsByCurrency;
    }

    private BigDecimal convertedAmount(Transaction tx) {
        return tx.getAmount().multiply(rateFor(tx));
    }

    private BigDecimal rateFor(Transaction tx) {
        // Why orElseThrow, not the seeded Optional.of(nullable): Optional.of(null) throws
        // NullPointerException for any null argument — that's Optional's own contract, not a
        // bug in Optional itself. The seeded bug was calling of() on a value the interface
        // never actually promised was non-null. Changing RateProvider to return
        // Optional<BigDecimal> (see that interface) makes "might not have a rate" part of the
        // compiled contract, and a named MissingRateException tells the reader what actually
        // went wrong instead of a bare NPE.
        return rateProvider.getRate(tx.getCurrency())
                .orElseThrow(() -> new MissingRateException(tx.getCurrency()));
    }

    // Why this returns CompletableFuture<Void> instead of void: the seeded version returned
    // void and kept no reference to the future it created — fire and forget. If
    // logTransaction() threw, the exception completed that future exceptionally and then just
    // sat there, unread by anyone, forever — nothing crashed, nothing logged, the audit entry
    // was simply missing. That's exactly "logs mysteriously missing entries with no error
    // anywhere." Returning the future lets the caller wait for it (as reconcileByCurrency now
    // does), and .exceptionally() gives a failure somewhere to go instead of vanishing.
    private CompletableFuture<Void> auditAsync(Transaction tx) {
        return CompletableFuture.runAsync(() -> externalAuditService.logTransaction(tx))
                .exceptionally(ex -> {
                    System.err.println("Audit failed for transaction " + tx.getId() + ": " + ex.getMessage());
                    return null;
                });
    }

    public Map<String, BigDecimal> amountsById(List<Transaction> transactions) {
        // Why duplicates are checked explicitly first, not left to Collectors.toMap's default
        // merge behaviour: the seeded version's crash (IllegalStateException: Duplicate key)
        // never named which transaction ID collided — see DuplicateTransactionException for
        // why that matters here.
        Map<String, Long> idCounts = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getId, Collectors.counting()));

        idCounts.forEach((id, count) -> {
            if (count > 1) {
                throw new DuplicateTransactionException(id);
            }
        });

        return transactions.stream()
                .collect(Collectors.toMap(Transaction::getId, Transaction::getAmount));
    }
}
