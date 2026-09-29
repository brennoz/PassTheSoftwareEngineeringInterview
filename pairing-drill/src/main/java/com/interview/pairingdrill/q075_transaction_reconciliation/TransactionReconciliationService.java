package com.interview.pairingdrill.q075_transaction_reconciliation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Map<String, BigDecimal> totalsByCurrency = new HashMap<>();

        transactions.parallelStream().forEach(tx -> {
            BigDecimal rate = rateFor(tx);
            totalsByCurrency.merge(tx.getCurrency(), tx.getAmount().multiply(rate), BigDecimal::add);
            auditAsync(tx);
        });

        return totalsByCurrency;
    }

    private BigDecimal rateFor(Transaction tx) {
        Optional<BigDecimal> rate = Optional.of(rateProvider.getRate(tx.getCurrency()));
        return rate.get();
    }

    private void auditAsync(Transaction tx) {
        CompletableFuture.supplyAsync(() -> {
            externalAuditService.logTransaction(tx);
            return null;
        });
    }

    public Map<String, BigDecimal> amountsById(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.toMap(Transaction::getId, Transaction::getAmount));
    }
}
