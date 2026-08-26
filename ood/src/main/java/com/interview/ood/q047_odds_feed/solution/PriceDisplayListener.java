package com.interview.ood.q047_odds_feed.solution;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Pattern: Observer (Concrete Observer) — the simplest possible reaction to an odds change:
// remember the latest price per selection, as a real price-display board would. Uses
// ConcurrentHashMap, not HashMap, for the same reason OddsFeed itself does: onOddsChanged()
// can be invoked from OddsFeed's publishing thread while a UI thread concurrently calls
// getLatestOdds() to render the board — a plain HashMap under that access pattern is a
// classic "works in dev, corrupts under load" distributed-system-style bug, even within a
// single process.
public class PriceDisplayListener implements OddsListener {

    private final Map<String, BigDecimal> latestOdds = new ConcurrentHashMap<>();

    @Override
    public void onOddsChanged(OddsChangedEvent event) {
        latestOdds.put(event.selectionName(), event.newOdds());
    }

    public BigDecimal getLatestOdds(String selectionName) {
        return latestOdds.get(selectionName);
    }
}
