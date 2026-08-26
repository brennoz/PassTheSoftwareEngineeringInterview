package com.interview.prep.natwest.ex1_thread_safe_ledger.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    @Test
    void withdrawReducesBalanceAndRejectsOverdraft() {
        Ledger ledger = new Ledger();
        ledger.open(new Account("acc-1", new BigDecimal("100.00")));

        ledger.withdraw("acc-1", new BigDecimal("40.00"));

        assertEquals(0, ledger.balanceOf("acc-1").compareTo(new BigDecimal("60.00")));
        assertThrows(InsufficientFundsException.class,
                () -> ledger.withdraw("acc-1", new BigDecimal("1000.00")));
    }

    // This is the test that actually proves thread-safety — a naive (unlocked) balance
    // field would lose updates here and land on some value greater than 0.00, because two
    // threads can both read the same balance before either writes it back (a classic
    // read-modify-write race). 50 threads * 20.00 exactly drains a 1000.00 balance to zero
    // ONLY if every withdraw is atomic end-to-end.
    @Test
    void concurrentWithdrawalsNeverLoseAnUpdate() throws InterruptedException {
        Ledger ledger = new Ledger();
        ledger.open(new Account("acc-1", new BigDecimal("1000.00")));

        int threadCount = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    ledger.withdraw("acc-1", new BigDecimal("20.00"));
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(done.await(5, TimeUnit.SECONDS));
        pool.shutdown();

        assertEquals(0, ledger.balanceOf("acc-1").compareTo(new BigDecimal("0.00")));
    }

    // Proves the lock-ordering fix in Ledger.transfer(): two threads transferring in
    // OPPOSITE directions between the same two accounts, repeatedly, must never deadlock.
    // assertTimeoutPreemptively fails the test (instead of hanging forever) if it does.
    @Test
    void oppositeDirectionTransfersDoNotDeadlock() {
        assertTimeoutPreemptively(ofSeconds(5), () -> {
            Ledger ledger = new Ledger();
            ledger.open(new Account("acc-1", new BigDecimal("10000.00")));
            ledger.open(new Account("acc-2", new BigDecimal("10000.00")));

            ExecutorService pool = Executors.newFixedThreadPool(2);
            CountDownLatch done = new CountDownLatch(2);

            pool.submit(() -> {
                for (int i = 0; i < 200; i++) {
                    ledger.transfer("acc-1", "acc-2", new BigDecimal("1.00"));
                }
                done.countDown();
            });
            pool.submit(() -> {
                for (int i = 0; i < 200; i++) {
                    ledger.transfer("acc-2", "acc-1", new BigDecimal("1.00"));
                }
                done.countDown();
            });

            done.await(5, TimeUnit.SECONDS);
            pool.shutdown();

            BigDecimal total = ledger.balanceOf("acc-1").add(ledger.balanceOf("acc-2"));
            assertEquals(0, total.compareTo(new BigDecimal("20000.00")));
        });
    }
}
