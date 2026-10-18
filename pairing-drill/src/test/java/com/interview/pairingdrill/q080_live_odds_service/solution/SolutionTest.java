package com.interview.pairingdrill.q080_live_odds_service.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionTest {

    @Mock OddsRepository oddsRepository;
    @Mock MarketRepository marketRepository;

    LiveOddsService service;

    @BeforeEach
    void setUp() {
        service = new LiveOddsService(oddsRepository, marketRepository);
    }

    // Verifies: BigDecimal odds stored and retrievable; repository receives correct snapshot.
    // A double-based version would appear to pass this test for round values like 2.50
    // (exact in IEEE 754), so the snapshot assertion matters more than getOdds() alone.
    @Test
    void should_store_odds_and_persist_snapshot() {
        service.acceptUpdate("m1", new BigDecimal("1.65"), 1L);

        assertEquals(0, service.getOdds("m1").compareTo(new BigDecimal("1.65")));

        ArgumentCaptor<OddsSnapshot> captor = ArgumentCaptor.forClass(OddsSnapshot.class);
        verify(oddsRepository).save(captor.capture());
        assertEquals("m1", captor.getValue().getMarketId());
        assertEquals(0, captor.getValue().getOdds().compareTo(new BigDecimal("1.65")));
    }

    // Verifies: suspended market blocks the update atomically — repo never called.
    // Without synchronized, a race between isSuspended-check and put() means a just-suspended
    // market can still receive an odds write; this test verifies the guard holds.
    @Test
    void should_block_update_on_suspended_market() {
        when(marketRepository.findById("m1"))
                .thenReturn(Optional.of(new Market("m1", MarketStatus.ACTIVE)));

        service.suspendMarket("m1");
        service.acceptUpdate("m1", new BigDecimal("2.00"), 2L);

        verify(oddsRepository, never()).save(any());
        assertTrue(service.isSuspended("m1"));
    }

    // Verifies: suspendMarket persists enum MarketStatus.SUSPENDED (not a raw String).
    // The ArgumentCaptor catches what was actually passed to save() so we can assert
    // the enum value — a String "SUSPENDED" would fail this assertion at the type level.
    @Test
    void should_persist_suspended_status_as_enum() {
        when(marketRepository.findById("m1"))
                .thenReturn(Optional.of(new Market("m1", MarketStatus.ACTIVE)));

        service.suspendMarket("m1");

        ArgumentCaptor<Market> captor = ArgumentCaptor.forClass(Market.class);
        verify(marketRepository).save(captor.capture());
        assertEquals(MarketStatus.SUSPENDED, captor.getValue().getStatus());
    }

    // Verifies: reopened market accepts updates again.
    // Covers the suspend → reopen → accept path; checks the set is mutated correctly.
    @Test
    void should_allow_updates_after_reopen() {
        when(marketRepository.findById("m1"))
                .thenReturn(Optional.of(new Market("m1", MarketStatus.ACTIVE)));

        service.suspendMarket("m1");
        service.reopenMarket("m1");
        service.acceptUpdate("m1", new BigDecimal("3.10"), 3L);

        assertFalse(service.isSuspended("m1"));
        verify(oddsRepository, times(1)).save(any());
    }

    // Verifies: updateCount is exact after concurrent acceptUpdate calls.
    // long++ is a read-modify-write (three instructions); concurrent threads can read the same
    // value before any write, increment it independently, and write back — count is less than N.
    // AtomicLong.incrementAndGet() uses a single CPU CAS — guaranteed exact count.
    // This test will non-deterministically fail on the seeded code (not every run catches the
    // race) but will never fail on the fixed AtomicLong version.
    @Test
    void should_count_updates_atomically_under_concurrency() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                service.acceptUpdate("m1", new BigDecimal("2.50"), 1L);
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
        assertEquals(100, service.getUpdateCount());
    }
}
