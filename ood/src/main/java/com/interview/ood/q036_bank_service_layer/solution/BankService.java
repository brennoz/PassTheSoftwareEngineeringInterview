package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.util.List;

// Pattern: Facade — single entry point for all banking operations.
// Hides the AccountService + TransferService split from callers (controllers, tests, Main).
// Callers interact with one object; the internal service boundary is an implementation detail.
//
// Why Facade and not just one big service?
//   AccountService and TransferService each have a clear single responsibility.
//   Keeping them separate means each is unit-testable in isolation (see their own test classes).
//   BankService composes them without duplicating logic — Open/Closed: adding a new service
//   (e.g. LoanService) means adding a new field here, not modifying existing services.
//
// In prod (Spring): @Service + constructor injection. BankService is what the @RestController
//   injects. Controllers should never inject AccountService directly — all traffic goes through
//   the facade so cross-cutting concerns (logging, metrics, tracing) can be added in one place.
//
// register() is a no-op here — in a real system this would create a Customer aggregate,
//   run KYC checks, and publish a CustomerRegistered event to Kafka.
public class BankService {

    private final AccountService  accountService;
    private final TransferService transferService;

    public BankService(AccountService accountService, TransferService transferService) {
        this.accountService  = accountService;
        this.transferService = transferService;
    }

    // No-op — in prod: create Customer, run KYC, publish CustomerRegistered event.
    public void register(String ownerId) { /* intentionally empty in this exercise */ }

    public Account openAccount(String ownerId, AccountType type) {
        return accountService.open(ownerId, type);
    }

    public Transaction deposit(String accountId, BigDecimal amount) {
        return accountService.deposit(accountId, amount);
    }

    public Transaction withdraw(String accountId, BigDecimal amount) {
        return accountService.withdraw(accountId, amount);
    }

    public Transfer transfer(String fromId, String toId, BigDecimal amount) {
        return transferService.transfer(fromId, toId, amount);
    }

    public BigDecimal getBalance(String accountId) {
        return accountService.getBalance(accountId);
    }

    public List<Transaction> history(String accountId) {
        return accountService.history(accountId);
    }

    public void closeAccount(String accountId) {
        accountService.close(accountId);
    }
}
