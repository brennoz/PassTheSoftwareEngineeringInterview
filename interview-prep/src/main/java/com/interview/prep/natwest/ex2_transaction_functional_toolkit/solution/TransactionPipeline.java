package com.interview.prep.natwest.ex2_transaction_functional_toolkit.solution;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

// The thenApply vs thenCompose exercise, in one class.
public class TransactionPipeline {

    private final AccountFetcher accountFetcher;
    private final TransactionValidator validator;

    public TransactionPipeline(AccountFetcher accountFetcher, TransactionValidator validator) {
        this.accountFetcher = accountFetcher;
        this.validator = validator;
    }

    // thenApply: the transform is a PLAIN, already-available value (BigDecimal), not another
    // async operation. thenApply(Function<T, R>) unwraps CompletableFuture<T> and maps it to
    // R directly — using thenCompose here would be wrong AND wouldn't compile as written,
    // since Account::getBalance doesn't return a CompletableFuture.
    public CompletableFuture<BigDecimal> fetchBalance(String accountId) {
        return accountFetcher.fetchAccount(accountId)
                .thenApply(Account::getBalance);
    }

    // thenCompose: the next step (validator.validate) is ITSELF async — it returns
    // CompletableFuture<ValidationResult>. thenCompose flattens that so the pipeline stays
    // CompletableFuture<ValidationResult>, not CompletableFuture<CompletableFuture<...>>.
    // Rule of thumb: thenApply for a sync transform, thenCompose when the next step returns
    // a CompletableFuture of its own (chaining two async calls end-to-end).
    public CompletableFuture<ValidationResult> validateTransaction(Transaction transaction) {
        return accountFetcher.fetchAccount(transaction.getAccountId())
                .thenCompose(account -> validator.validate(account, transaction));
    }
}
