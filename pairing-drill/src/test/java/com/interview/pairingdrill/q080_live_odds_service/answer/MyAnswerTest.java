package com.interview.pairingdrill.q080_live_odds_service.answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyAnswerTest {

    private @Mock OddsRepository oddsRepository;
    private @Mock MarketRepository marketRepository;

    private LiveOddsService service;

    @BeforeEach
    void setUp() {
        service = new LiveOddsService(oddsRepository, marketRepository);
    }

    @Test
    void should_store_odds_and_persist_snapshot() {

        service.acceptUpdate("m1", new BigDecimal("1.65"), 1L);
        assertEquals(0, service.getOdds("m1").compareTo(new BigDecimal("1.65")));

        ArgumentCaptor<OddsSnapshot> captor = ArgumentCaptor.forClass(OddsSnapshot.class);
        verify(oddsRepository).save(captor.capture());
        assertEquals("m1", captor.getValue().getMarketId());
        assertEquals(0, captor.getValue().getOdds().compareTo(new BigDecimal("1.65")));
    }
    
    @Test
    void should_block_update_on_suspended_market() {
        when(marketRepository.findById("m1"))
                .thenReturn(Optional.of(new Market("m1", MarketStatus.ACTIVE)));

        service.suspendMarket("m1");
        service.acceptUpdate("m1", new BigDecimal("2.00"), 2L);

        verify(oddsRepository, never()).save(any());
        assertTrue(service.isSuspended("m1"));
    }  

    @Test
    void should_persist_suspended_status_as_enum() {
        when(marketRepository.findById("m1"))
                .thenReturn(Optional.of(new Market("m1", MarketStatus.ACTIVE)));

        service.suspendMarket("m1");

        ArgumentCaptor<Market> captor = ArgumentCaptor.forClass(Market.class);
        verify(marketRepository).save(captor.capture());
        assertEquals(MarketStatus.SUSPENDED, captor.getValue().getStatus());
    }
    
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
        assertEquals(100, service.getUpdateCount().longValue());
    }    
}
