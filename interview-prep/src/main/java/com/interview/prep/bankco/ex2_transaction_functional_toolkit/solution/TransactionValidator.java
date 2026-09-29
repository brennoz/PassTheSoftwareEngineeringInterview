package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

import java.util.concurrent.CompletableFuture;

// Also async (imagine a downstream fraud-check API call) — this is what forces the
// thenCompose choice in TransactionPipeline: this method ALREADY returns a
// CompletableFuture, so chaining it after another async step with thenApply would produce
// CompletableFuture<CompletableFuture<ValidationResult>>, not the flat, usable
// CompletableFuture<ValidationResult> callers actually want.
public class TransactionValidator {

    public CompletableFuture<ValidationResult> validate(Account account, Transaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            if (!account.isActive()) {
                return ValidationResult.rejected("account inactive");
            }
            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                return ValidationResult.rejected("insufficient funds");
            }
            return ValidationResult.approved();
        });
    }
}
