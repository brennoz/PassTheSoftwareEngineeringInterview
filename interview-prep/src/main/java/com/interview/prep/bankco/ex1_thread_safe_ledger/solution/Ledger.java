package com.interview.prep.bankco.ex1_thread_safe_ledger.solution;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// Pattern: per-account ReentrantLock, not synchronized — gives us tryLock(timeout), which
// synchronized cannot do. A live-coding interviewer testing "locks in general" is very likely
// probing whether you reach for synchronized reflexively or know why ReentrantLock exists.
public class Ledger {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private static final long LOCK_TIMEOUT_MS = 500;

    public void open(Account account) {
        accounts.put(account.getId(), account);
    }

    public BigDecimal balanceOf(String accountId) {
        return account(accountId).getBalance();
    }

    public void withdraw(String accountId, BigDecimal amount) {
        Account account = account(accountId);
        withLock(account, () -> {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountId);
            }
            account.setBalance(account.getBalance().subtract(amount));
        });
    }

    public void deposit(String accountId, BigDecimal amount) {
        Account account = account(accountId);
        withLock(account, () -> account.setBalance(account.getBalance().add(amount)));
    }

    // Algorithm: lock ordering by account id — the classic fix for the dining-philosophers
    // deadlock. If thread A does transfer(x, y) while thread B does transfer(y, x) at the same
    // moment, and each locks its "from" account first, A holds x waiting for y while B holds y
    // waiting for x — deadlock. Always acquiring locks in the same global order (here: id
    // comparison) means both threads try to lock the SAME account first, so one of them simply
    // waits its turn instead of the two threads deadlocking on each other.
    public void transfer(String fromId, String toId, BigDecimal amount) {
        Account first = account(fromId.compareTo(toId) <= 0 ? fromId : toId);
        Account second = account(fromId.compareTo(toId) <= 0 ? toId : fromId);

        lock(first);
        try {
            lock(second);
            try {
                Account from = account(fromId);
                Account to = account(toId);
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException(fromId);
                }
                from.setBalance(from.getBalance().subtract(amount));
                to.setBalance(to.getBalance().add(amount));
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    private void withLock(Account account, Runnable action) {
        lock(account);
        try {
            action.run();
        } finally {
            account.getLock().unlock();
        }
    }

    private void lock(Account account) {
        ReentrantLock lock = account.getLock();
        try {
            if (!lock.tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                throw new LockTimeoutException(account.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockTimeoutException(account.getId());
        }
    }

    private Account account(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Unknown account: " + accountId);
        }
        return account;
    }
}
