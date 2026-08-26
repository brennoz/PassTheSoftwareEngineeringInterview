package com.interview.ood.q047_odds_feed.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    @Test
    void firstPublishForANewSelectionHasNoBaselineAndDoesNotSuspend() {
        OddsFeed feed = new OddsFeed();
        PriceDisplayListener priceDisplay = new PriceDisplayListener();
        MarketSuspensionListener suspensionMonitor = new MarketSuspensionListener(new BigDecimal("20"));
        feed.addListener(priceDisplay);
        feed.addListener(suspensionMonitor);

        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.00"));

        // Why compareTo, not assertEquals directly on BigDecimal: BigDecimal.equals() is
        // scale-sensitive ("2.00" != "2.0"), a standing rule in this project.
        assertEquals(0, priceDisplay.getLatestOdds("Home").compareTo(new BigDecimal("2.00")));
        assertFalse(suspensionMonitor.isSuspended("Home"));
    }

    @Test
    void moveBelowThresholdDoesNotSuspend() {
        OddsFeed feed = new OddsFeed();
        MarketSuspensionListener suspensionMonitor = new MarketSuspensionListener(new BigDecimal("20"));
        feed.addListener(suspensionMonitor);

        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.00"));
        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.10")); // 5% move

        assertFalse(suspensionMonitor.isSuspended("Home"));
    }

    // What this verifies: the percentage calculation, not just "any move suspends." A
    // weaker test using a huge raw difference on long odds (e.g. 10.00 -> 10.05) would pass
    // under a buggy raw-difference-based implementation too — this test's move is
    // deliberately exactly at the configured threshold to prove the >= boundary is honoured,
    // not just moves comfortably past it.
    @Test
    void moveAtOrAboveThresholdSuspendsTheSelection() {
        OddsFeed feed = new OddsFeed();
        MarketSuspensionListener suspensionMonitor = new MarketSuspensionListener(new BigDecimal("20"));
        feed.addListener(suspensionMonitor);

        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.00"));
        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.40")); // exactly 20% move

        assertTrue(suspensionMonitor.isSuspended("Home"));
    }

    // What this verifies: OddsFeed notifies every registered listener from one published
    // event, and each listener reacts independently — the actual point of Observer over a
    // single feed with one hardcoded reaction. A design without Observer would need OddsFeed
    // itself to know about both a price board and a suspension monitor.
    @Test
    void singlePublishNotifiesAllRegisteredListenersIndependently() {
        OddsFeed feed = new OddsFeed();
        PriceDisplayListener priceDisplay = new PriceDisplayListener();
        MarketSuspensionListener suspensionMonitor = new MarketSuspensionListener(new BigDecimal("20"));
        feed.addListener(priceDisplay);
        feed.addListener(suspensionMonitor);

        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.00"));
        feed.publishOddsChange("Match A", "Home", new BigDecimal("3.50")); // 75% move

        assertEquals(0, priceDisplay.getLatestOdds("Home").compareTo(new BigDecimal("3.50")));
        assertTrue(suspensionMonitor.isSuspended("Home"));
    }

    // What this verifies: a listener may unregister itself from inside its own
    // onOddsChanged() callback, mid-publish, without the feed throwing
    // ConcurrentModificationException or skipping notification of the remaining listeners.
    // This is the actual reason OddsFeed uses CopyOnWriteArrayList rather than a plain
    // ArrayList — a test using a plain HashMap-backed list would only fail intermittently
    // or not at all depending on iteration internals, so this asserts the concrete
    // observable behaviour (call counts) rather than the collection type itself.
    @Test
    void listenerCanUnregisterItselfDuringNotificationWithoutBreakingTheFeed() {
        OddsFeed feed = new OddsFeed();
        AtomicInteger selfRemovingCallCount = new AtomicInteger(0);
        AtomicInteger otherCallCount = new AtomicInteger(0);

        OddsListener selfRemoving = new OddsListener() {
            @Override
            public void onOddsChanged(OddsChangedEvent event) {
                selfRemovingCallCount.incrementAndGet();
                feed.removeListener(this);
            }
        };
        OddsListener other = event -> otherCallCount.incrementAndGet();

        feed.addListener(selfRemoving);
        feed.addListener(other);

        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.00"));
        feed.publishOddsChange("Match A", "Home", new BigDecimal("2.10"));

        assertEquals(1, selfRemovingCallCount.get());
        assertEquals(2, otherCallCount.get());
    }
}
