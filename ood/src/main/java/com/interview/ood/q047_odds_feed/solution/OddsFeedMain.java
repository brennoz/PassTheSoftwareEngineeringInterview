package com.interview.ood.q047_odds_feed.solution;

import java.math.BigDecimal;

// Runnable demo — not a test. Walks a live in-play sequence: a small odds drift that stays
// under the suspension threshold, then a drastic move that trips it, then a listener
// unregistering itself mid-feed (the case CopyOnWriteArrayList in OddsFeed exists for).
public class OddsFeedMain {

    public static void main(String[] args) {
        OddsFeed feed = new OddsFeed();
        PriceDisplayListener priceDisplay = new PriceDisplayListener();
        MarketSuspensionListener suspensionMonitor = new MarketSuspensionListener(new BigDecimal("20"));

        feed.addListener(priceDisplay);
        feed.addListener(suspensionMonitor);

        System.out.println("=== First price for a new selection (no prior baseline) ===");
        feed.publishOddsChange("Arsenal vs Chelsea", "Arsenal to win", new BigDecimal("2.00"));
        System.out.println("Price board shows: " + priceDisplay.getLatestOdds("Arsenal to win"));
        System.out.println("Suspended: " + suspensionMonitor.isSuspended("Arsenal to win"));

        System.out.println();
        System.out.println("=== Small drift, well under the 20% threshold ===");
        feed.publishOddsChange("Arsenal vs Chelsea", "Arsenal to win", new BigDecimal("2.10"));
        System.out.println("Price board shows: " + priceDisplay.getLatestOdds("Arsenal to win"));
        System.out.println("Suspended: " + suspensionMonitor.isSuspended("Arsenal to win"));

        System.out.println();
        System.out.println("=== Drastic move (red card just happened) -> market suspends ===");
        feed.publishOddsChange("Arsenal vs Chelsea", "Arsenal to win", new BigDecimal("3.50"));
        System.out.println("Price board shows: " + priceDisplay.getLatestOdds("Arsenal to win"));
        System.out.println("Suspended: " + suspensionMonitor.isSuspended("Arsenal to win"));

        System.out.println();
        System.out.println("=== Price display listener unregisters itself; feed keeps publishing ===");
        feed.removeListener(priceDisplay);
        feed.publishOddsChange("Arsenal vs Chelsea", "Arsenal to win", new BigDecimal("3.60"));
        System.out.println("Price board (stale, listener removed): " + priceDisplay.getLatestOdds("Arsenal to win"));
        System.out.println("Suspended (still updates via the remaining listener): "
                + suspensionMonitor.isSuspended("Arsenal to win"));
    }
}
