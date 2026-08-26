package com.interview.algorithms.java_features.q051_concurrent_event_counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    private MyAnswer myAnswer;

    @BeforeEach
    void init () {
        myAnswer = new MyAnswer();
    }

    @Test
    void recordIncrementsCountForThatEvent() {

        myAnswer.record("login");
        myAnswer.record("login");
        myAnswer.record("logout");

        assertEquals(2, myAnswer.countOf("login"));
        assertEquals(1, myAnswer.countOf("logout"));
    }

    @Test
    void unrecordedEventReturnsZero() {

        assertEquals(0, myAnswer.countOf("never-happened"));
    }

    @Test
    void concurrentRecordsToSameEventLoseNoUpdates() throws InterruptedException {
        
        int threads = 10;
        int callsPerThread = 1000;

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < callsPerThread; i++) {
                        myAnswer.record("checkout");
                    }
                });
            }
            pool.shutdown();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS), "pool did not finish in time");
        }

        assertEquals(threads * callsPerThread, myAnswer.countOf("checkout"));
    }
}
