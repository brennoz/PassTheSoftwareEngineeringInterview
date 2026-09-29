package com.interview.pairingdrill.q075_transaction_reconciliation.answer;

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

        Map<String, BigDecimal> totalsByCurrency = transactions.stream()
                                                            .collect(Collectors.groupingBy(
                                                                Transaction::getCurrency,
                                                                Collectors.reducing(BigDecimal.ZERO, tx -> tx.getAmount().multiply(rateFor(tx.getCurrency())), BigDecimal::add)
                                                            ));

        List<CompletableFuture<Void>> auditFutures = transactions.stream()
                                                            .map(this::auditAsync)
                                                            .toList();

        CompletableFuture.allOf(auditFutures.toArray(new CompletableFuture[0])).join();

        return totalsByCurrency;
    }

    private BigDecimal rateFor(String currency) {
        return rateProvider.getRate(currency).orElseThrow(() -> new MissingRateException(currency));
    }

    private CompletableFuture<Void> auditAsync(Transaction tx) {
        return CompletableFuture.runAsync(() -> externalAuditService.logTransaction(tx))
            .exceptionally(ex -> {
                System.err.println("Audit failed for transaction " + tx.getId() + ": " + ex.getMessage());
                return null;
            });
    }

    public Map<String, BigDecimal> amountsById(List<Transaction> transactions) {
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
