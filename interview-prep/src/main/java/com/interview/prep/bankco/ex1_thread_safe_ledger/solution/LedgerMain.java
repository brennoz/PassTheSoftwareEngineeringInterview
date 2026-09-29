package com.interview.prep.bankco.ex1_thread_safe_ledger.solution;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Runnable demo — not a test. Spins up concurrent threads hammering the same account and
// prints the final balance, so you can SEE the lock working (or, if you comment it out,
// see the lost-update bug happen) rather than just trusting the assertion in SolutionTest.
public class LedgerMain {

    public static void main(String[] args) throws InterruptedException {
        Ledger ledger = new Ledger();
        ledger.open(new Account("acc-1", new BigDecimal("1000.00")));
        ledger.open(new Account("acc-2", new BigDecimal("500.00")));

        int threadCount = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    ledger.withdraw("acc-1", new BigDecimal("10.00"));
                } finally {
                    done.countDown();
                }
            });
        }

        done.await(5, TimeUnit.SECONDS);
        pool.shutdown();

        System.out.println("acc-1 balance after 20 concurrent withdrawals of 10.00: "
                + ledger.balanceOf("acc-1") + " (expected 800.00)");

        ledger.transfer("acc-1", "acc-2", new BigDecimal("100.00"));
        System.out.println("acc-1 after transfer: " + ledger.balanceOf("acc-1"));
        System.out.println("acc-2 after transfer: " + ledger.balanceOf("acc-2"));
    }
}
